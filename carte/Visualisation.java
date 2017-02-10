package carte;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import graphe.*;

public class Visualisation extends JFrame {

  private static final long serialVersionUID = 1L;
  Set<Ville> sommets = null;
 // GrapheEuclidien graphe = null;
  Collection<Arc> arcs = null;
  Map<Ville, Color> couleurVilles = null;
  Map<Arc, Color> couleurArcs = null;
  double latmin = -180, latmax = 180, lonmin = -90, lonmax = 90;
  JComponent dessin;
  JPanel panel;
  // JComboBox_ boite;
  boolean afficheNoms = false;
  int ovalRadius =1;

  void centrage(double x, double y) {
    double deltaX = (latmax + latmin) / 2.0
        - (latmin + (latmax - latmin) * (1 - y));
    double deltaY = (lonmax + lonmin) / 2.0 - (lonmin + (lonmax - lonmin) * x);
    System.out.println(x + " " + y + " " + deltaX + " " + deltaY);
    System.out.println(lonmin + " " + lonmax + " " + latmin + " " + latmax);
    latmin -= deltaX;
    latmax -= deltaX;
    lonmin -= deltaY;
    lonmax -= deltaY;
    System.out.println(lonmin + " " + lonmax + " " + latmin + " " + latmax);
    repaint();
  }

  void zoomIn() {
    double centreX = (latmin + latmax) / 2.0;
    double centreY = (lonmin + lonmax) / 2.0;
    double deltaX = latmax - centreX;
    double deltaY = lonmax - centreY;
    latmin = centreX - .9 * deltaX;
    latmax = centreX + .9 * deltaX;
    lonmin = centreY - .9 * deltaY;
    lonmax = centreY + .9 * deltaY;
    repaint();
  }

  void zoomOut() {
    double centreX = (latmin + latmax) / 2.0;
    double centreY = (lonmin + lonmax) / 2.0;
    double deltaX = latmax - centreX;
    double deltaY = lonmax - centreY;
    latmin = centreX - deltaX / .9;
    latmax = centreX + deltaX / .9;
    lonmin = centreY - deltaY / .9;
    lonmax = centreY + deltaY / .9;
    repaint();
  }
  
  public void setOvalRadius(int i){
	  ovalRadius=i;
  }
  
  public int getOvalRadius(){
	  return ovalRadius;
  }

  public Visualisation(String titre) {
    super(titre);
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    System.setProperty("swing.aatext", "true");
    dessin = new PanneauDessin(this);
    add("Center", dessin);
    JMenuBar menuBar = new JMenuBar();
    JMenu mFichier = new JMenu("Fichier");
    menuBar.add(mFichier);
    JMenuItem itemQuit = new JMenuItem("Quit");
    mFichier.add(itemQuit);
    itemQuit.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    JMenu mAffiche = new JMenu("Affichage");
    menuBar.add(mAffiche);
    JMenuItem item = new JMenuItem("Affichage nom villes");
    mAffiche.add(item);
    item.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        afficheNoms(!afficheNoms);
        repaint();
      }
    });
    JMenuItem itemCadrage = new JMenuItem("Recadrage");
    mAffiche.add(itemCadrage);
    itemCadrage.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        calculLimites();
        repaint();
      }
    });
    setJMenuBar(menuBar);
    panel = new JPanel();
    add("South", panel);
    definirSommets(new HashSet<Ville>());
    synchronized(getTreeLock())
    {validateTree();}
    setVisible(true);
  }

  public void definirSommets(Set<Ville> villes) {
    panel.removeAll();
    sommets = villes;
    calculLimites();

    // if (!sommets.isEmpty()) {
      Vector<String> tab = new Vector<String>(sommets.size());
      for (Ville v : sommets) {
        tab.add(v.getNom());
      }
      if (sommets.isEmpty()) tab.add("Pas de sommet");
      // JPanel panel = new JPanel();
      final JComboBox boite = new JComboBox_(tab);
      panel.add(boite);

      JButton valide = new JButton("OK");
      valide.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          // On se centre sur la ville
          double deltaLat2 = (latmax - latmin) / 2.0;
          double deltalon2 = (lonmax - lonmin) / 2.0;
          String s = (String) boite.getSelectedItem();
          Ville v = null;
          for (Ville vv : sommets)
            if (vv.getNom().equals(s))
              v = vv;
          if (v != null) {
            double latCentre = v.getLatitude();
            double lonCentre = v.getLongitude();
            latmin = latCentre - deltaLat2;
            latmax = latCentre + deltaLat2;
            lonmin = lonCentre - deltalon2;
            lonmax = lonCentre + deltalon2;
            repaint();
          }
        }
      });
      panel.add(valide);
      panel.validate();
      dessin.repaint();
      // add("South", panel);
      // invalidate();
      // validateTree();
    // }
  }

  public void couleurVilles(Map<Ville, Color> c) {
    couleurVilles = c;
    dessin.repaint();
  }

  public void definirArcs(Collection<Arc> a) {
    arcs = a;
    panel.validate();
  }

  public void definirGraphe(GrapheEuclidien g) {
    // graphe = g;
     Collection<Arc> newArcs = new LinkedList<Arc>();
     for (Ville v:g.villes()) for (Arc a :g.arcsSortant(v)) newArcs.add(a);
     arcs = newArcs;
     definirSommets(g.villes());
  }

  public void couleurArcs(Map<Arc, Color> c) {
    couleurArcs = c;
    dessin.repaint();
  }

  void afficheNoms(boolean b) {
    afficheNoms = b;
  }

  void calculLimites() {
    double llatmin = 0, llatmax = 0, llonmin = 0, llonmax = 0;
    int i = 0;

    if (sommets == null || sommets.size() == 0)
      return;

    for (Ville v : sommets) {
      if (i == 0) {
        llatmin = v.getLatitude();
        llatmax = v.getLatitude();
        llonmin = v.getLongitude();
        llonmax = v.getLongitude();
      }
      i++;

      if (v.getLatitude() < llatmin)
        llatmin = v.getLatitude();
      if (v.getLatitude() > llatmax)
        llatmax = v.getLatitude();
      if (v.getLongitude() < llonmin)
        llonmin = v.getLongitude();
      if (v.getLongitude() > llonmax)
        llonmax = v.getLongitude();
    }
    latmin = llatmin;
    latmax = llatmax;
    lonmin = llonmin;
    lonmax = llonmax;
  }
}

class PanneauDessin extends JComponent implements MouseListener {
  private static final long serialVersionUID = 1L;
  Visualisation fenetre;

  PanneauDessin(Visualisation f) {
    fenetre = f;
    setOpaque(true);
    setBackground(Color.white);
    addMouseListener(this);
  }

  
  public void paintComponent(Graphics g) {
	Color savedColor = g.getColor();
	Color TRANSLUCENT_GRAY = new Color(192, 192, 192, 64);
	if (fenetre.couleurVilles!=null) savedColor=TRANSLUCENT_GRAY;
    super.paintComponent(g);
    if (g instanceof Graphics2D) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
    }
    if (fenetre.sommets == null)
      return;
    
    // On affiche les aretes
    // if (fenetre.graphe != null) {
    if (fenetre.arcs != null) {
      //if (fenetre.couleurArcs != null) {
        // d'abord avec la couleur par defaut
        //Color defColor = fenetre.couleurArcs.get(null);
        //if (defColor != null)
        //  g.setColor(defColor);
      //}
    	
      g.setColor(savedColor);
      // toutes les aretes
      for (Arc a : fenetre.arcs)
        if(a.source!=null && a.target!=null)
        a.source.dessineArete(g, fenetre.latmin, fenetre.lonmin, fenetre.latmax,
            fenetre.lonmax, getWidth(), getHeight(), a.target);
      /*
      for (Ville v : fenetre.sommets) {
        Collection<Arc> voisins = fenetre.graphe.arcsSortant(v);
        if (voisins != null)
          for (Arc a : voisins) {
            v.dessineArete(g, fenetre.latmin, fenetre.lonmin, fenetre.latmax,
                fenetre.lonmax, getWidth(), getHeight(), a.target);
          }
      }
      */
      // puis on retrace les aretes qui ont une couleur particuliere
      if (fenetre.couleurArcs != null)
        for (Map.Entry<Arc, Color> entry : fenetre.couleurArcs.entrySet()) {
          Arc a = entry.getKey();
          
          g.setColor(entry.getValue());
          a.source.dessineArete(g, fenetre.latmin, fenetre.lonmin,
              fenetre.latmax, fenetre.lonmax, getWidth(), getHeight(),
              a.target);
        }
      g.setColor(savedColor);
    }
    // puis les villes
    for (Ville v : fenetre.sommets) {
      Color c = null;
      if (fenetre.couleurVilles != null)
        c = fenetre.couleurVilles.get(v);
      if (c != null)
        g.setColor(c);
      else
        g.setColor(savedColor);
      v.dessine(g, fenetre.latmin, fenetre.lonmin, fenetre.latmax,
          fenetre.lonmax, getWidth(), getHeight(), fenetre.afficheNoms,fenetre.getOvalRadius());
    }
    g.setColor(savedColor);
  }

  
  public void mouseClicked(MouseEvent e) {
    int bouton = e.getButton();
    if (bouton == MouseEvent.BUTTON1)
      fenetre.zoomIn();
    else if (bouton == MouseEvent.BUTTON3)
      fenetre.zoomOut();
    else
      fenetre.centrage(((double) e.getX()) / getWidth(), ((double) e.getY())
          / getHeight());
  }

  
  public void mouseEntered(MouseEvent e) { //
  }

  
  public void mouseExited(MouseEvent e) { //
  }

  
  public void mousePressed(MouseEvent e) { //
  }

  
  public void mouseReleased(MouseEvent e) { //
  }
}

class JComboBox_ extends JComboBox {
  /**
     * 
     */
  private static final long serialVersionUID = 1L;
  public int caretPos = 0;
  public JTextField tf = null;

  // public JComboBox_(final Object items[]) {
  public JComboBox_(final Vector<String> items) {
    super(items);
    this.setEditor(new BasicComboBoxEditor());
    this.setEditable(true);
  }

  
  public void setSelectedIndex(int ind) {
    super.setSelectedIndex(ind);
    tf.setText(getItemAt(ind).toString());
    tf.setSelectionEnd(caretPos + tf.getText().length());
    tf.moveCaretPosition(caretPos);
  }

  
  public void setEditor(ComboBoxEditor anEditor) {
    super.setEditor(anEditor);
    if (anEditor.getEditorComponent() instanceof JTextField) {
      tf = (JTextField) anEditor.getEditorComponent();
      tf.addKeyListener(new KeyAdapter() {
        
        public void keyReleased(KeyEvent ev) {
          char key = ev.getKeyChar();
          if (!(Character.isLetterOrDigit(key) || Character.isSpaceChar(key)))
            return;
          caretPos = tf.getCaretPosition();
          String text = "";
          try {
            text = tf.getText(0, caretPos);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          int n = getItemCount();
          for (int i = 0; i < n; i++) {
            int ind = ((String) getItemAt(i)).indexOf(text);
            if (ind == 0) {
              setSelectedIndex(i);
              return;
            }
          }
        }
      });
    }
  }

}
