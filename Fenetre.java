import javax.swing.*;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.*;

public class Fenetre extends JFrame {
  ImageFiltree img= null;
  Image img_chargee= null;
  ImageFiltree seuil= null;

  public Fenetre(){

    this.setTitle("Projet java");
    this.setSize(900, 600);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);

    JTextField txt = new JTextField("");
    JPanel panneau= new JPanel();
    JPanel panneau_bouton = new JPanel();
    JPanel panneau_image= new JPanel();
    JPanel container= new JPanel();
    JPanel panneau_res= new JPanel();
    JPanel panneau_scroll= new JPanel();
    JPanel panneau_int= new JPanel();
    JPanel panneau_int2= new JPanel();
    JPanel panneau_int3= new JPanel();
    JPanel panneau_filtre= new JPanel();
    JPanel panneau_scroll_vect= new JPanel();

    JLabel texte = new JLabel("PATH fichier");
    txt.setPreferredSize(new Dimension(150, 30));
    JButton load= new JButton("charger");

    load.addActionListener(new ActionListener(){
     public void actionPerformed(ActionEvent e){
      img= null;
      img_chargee= null;
      seuil= null;
      panneau_res.repaint();
      chargeimg(txt.getText());
      if(img_chargee != null)
      {
        afficheimg(img_chargee, panneau_image);
      }
      else
        panneau_image.repaint();
     }
    });


    JRadioButton Sobel = new JRadioButton("sobel");
    JRadioButton Prewitt = new JRadioButton("Prewitt");
    ButtonGroup group = new ButtonGroup();
    group.add(Sobel);
    group.add(Prewitt);

    JButton bouton_filtrer = new JButton("Filtrer");
    bouton_filtrer.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        if(img_chargee == null)
          erreur("aucune image chargee");;
        ImageFiltree tmp= new ImageFiltree((BufferedImage) img_chargee);
        ImageFiltree s_gris = new ImageFiltree(tmp.filtreGris());
        if(Sobel.isSelected())
          img = new ImageFiltree(s_gris.filtrePrewitt());
        else if(Prewitt.isSelected())
          img= new ImageFiltree(s_gris.filtreSobel());
        else
          erreur("aucun filtre selectionné");
        afficheimg(img.getImage(), panneau_res);
      }
    });


    // JButton bouton_gris = new JButton("Passer en gris");
    // bouton_gris.addActionListener(new ActionListener(){
    //   public void actionPerformed(ActionEvent e){
    //     if(img_chargee == null)
    //       erreur("aucune image chargée");
    //     ImageFiltree tmp= new ImageFiltree((BufferedImage) img_chargee);
    //     ImageFiltree s_gris = new ImageFiltree(tmp.filtreGris());
    //     afficheimg(s_gris.getImage(), panneau_res);
    //   }
    // });


    JSlider scroll_vect= new JSlider(JSlider.HORIZONTAL,0, 100, 100);
    scroll_vect.setMajorTickSpacing(50);
    scroll_vect.setMinorTickSpacing(10);
    scroll_vect.setPaintTicks(true);
    scroll_vect.setPaintLabels(true);
    scroll_vect.setValue(50);

    JSlider scroll= new JSlider(JSlider.HORIZONTAL,0, 255, 100);
    scroll.setMajorTickSpacing(255);
    scroll.setMinorTickSpacing(50);
    scroll.setPaintTicks(true);
    scroll.setPaintLabels(true);


    JButton bouton_seuil= new JButton("Seuil");
    bouton_seuil.addActionListener(new ActionListener(){
     public void actionPerformed(ActionEvent e){
      if(img == null)
        erreur("veuillez charger une image et appliquer un filtre");
      seuil = new ImageFiltree(img.seuil(scroll.getValue()));
      afficheimg(seuil.getImage(), panneau_res);
     }
    });

    JButton bouton_v1= new JButton("Vectoriser v1");
    bouton_v1.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        if(seuil != null)
        {
          ImageVectorisee svg = new ImageVectorisee(seuil.getImage());
          svg.vectorisation_v1();
        }
        else
          erreur("veuillez appliquer un seuil auparavant");
      }
    });

    JButton bouton_v2= new JButton("Vectoriser v2");
    bouton_v2.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        if(seuil != null)
        {
          ImageVectorisee svg = new ImageVectorisee(seuil.getImage());
          double tmp= scroll_vect.getValue();
          tmp= tmp/100;
          svg.vectorisation_v2(tmp);
        }
        else
          erreur("veuillez appliquer un seuil auparavant");
      }
    });

    panneau_bouton.add(texte);
    panneau_bouton.add(txt);
    panneau_bouton.add(load);
    // panneau_bouton.add(bouton_gris);

    panneau_filtre.add(Sobel);
    panneau_filtre.add(Prewitt);
    panneau_filtre.add(bouton_filtrer);

    panneau_scroll.add(scroll);
    panneau_scroll.add(bouton_seuil);

    panneau_scroll_vect.add(bouton_v1);
    panneau_scroll_vect.add(scroll_vect);
    panneau_scroll_vect.add(bouton_v2);

    container.setLayout(new GridLayout(1,2));
    container.add(panneau_image);
    container.add(panneau_res);

    panneau_int3.setLayout(new BorderLayout());
    panneau_int3.add(panneau_scroll_vect, BorderLayout.NORTH);
    panneau_int3.add(container);

    panneau_int2.setLayout(new BorderLayout());
    panneau_int2.add(panneau_scroll, BorderLayout.NORTH);
    panneau_int2.add(panneau_int3);

    panneau_int.setLayout(new BorderLayout());
    panneau_int.add(panneau_filtre, BorderLayout.NORTH);
    panneau_int.add(panneau_int2);

    panneau.setLayout(new BorderLayout());
    panneau.add(panneau_bouton, BorderLayout.NORTH);
    panneau.add(panneau_int);

    this.setContentPane(panneau);
    this.setVisible(true);
  }


  void chargeimg(String link){
    try{
      img_chargee = ImageIO.read(new File(link));
    } catch (IOException err) {
      JOptionPane.showMessageDialog(this, "image introuvable");
    }
  }

  void erreur(String msg)
  {
    JOptionPane.showMessageDialog(this, msg);
  }

  void afficheimg(Image img,JPanel j){
    Graphics g= j.getGraphics();
    BufferedImage imgb= (BufferedImage) img;
    double ratio= Math.min((double) j.getHeight()/imgb.getHeight(), (double) j.getWidth()/imgb.getWidth());
    int gx= j.getWidth()-(int) (imgb.getWidth()*ratio);
    int gy= j.getHeight()-(int) (imgb.getHeight()*ratio);
    g.drawImage(img, gx/2, gy, (int) (imgb.getWidth()*ratio), (int) (imgb.getHeight()*ratio), this);
  }
}
