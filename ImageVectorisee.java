import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class Point
{
  protected double x;
  protected double y;

  public Point(int i, int j)
  {
    x = i;
    y = j;
  }

  public Point(double i, double j)
  {
    x = i;
    y = j;
  }

  public double getX()
  {
    return x;
  }

  public double getY()
  {
    return y;
  }

  public String toString()
  {
    return "(" + x + "," + y + ")";
  }

  public boolean equals(Point obj)
  {
    if(x == obj.getX() && y == obj.getY())
      return true;
    else
      return false;
  }

  /* retourne la distance entre deux pixels */
  public double distance(Point obj)
  {
    return Math.sqrt((obj.getX() - x) * (obj.getX() - x) + (obj.getY() - y)*(obj.getY() - y));
  }
}

class SegmentSVG  // segment simple
{
  protected ArrayList<Point> points = new ArrayList<Point>(); // point au milieu

  public SegmentSVG()
  {

  }

  public SegmentSVG(Point p1, Point p2)
  {
    points.add(p1);
    points.add(p2);
  }

  public void add(Point p)
  {
    points.add(p);
  }

  public Point getPremierPoint()
  {
    return points.get(0);
  }

  public Point getDernierPoint()
  {
    return points.get(points.size() - 1);
  }

  public String toString()
  {
    int i;
    String s = "<polyline fill=\"none\" stroke=\"white\" points=\"";

    for(i = 0; i < points.size(); i++)
    {
      s += points.get(i).getX() + "," + points.get(i).getY() + " ";
    }

    s += "\"/>";

    return s;
  }
}

class DroiteSVG extends SegmentSVG
{
  protected Point droite; // ax + b

  public DroiteSVG()
  {

  }

  public DroiteSVG(Point p1, Point p2, Point droite1)
  {
    points.add(p1);
    points.add(p2);
    droite = droite1;
  }

  public static Point moindre_carre(ArrayList<Point> pixels)
  {
    double x, y, sx = 0, sy = 0, sxx = 0, sxy = 0, res, y_temporaire;

    int i, n = pixels.size();

    for(i = 0; i < pixels.size(); i++)
    {
      sx = sx + pixels.get(i).getX();
      sy = sy + pixels.get(i).getY();
      sxy = sxy +  pixels.get(i).getX() * pixels.get(i).getY();
      sxx = sxx + pixels.get(i).getX() * pixels.get(i).getX();
    }

    x = (sx * sy - n * sxy) / (sx * sx - n * sxx);
    y = (sy - x * sx) / pixels.size();

    ArrayList<Integer> residus = new ArrayList<Integer>(pixels.size());

    for(i = 0; i < pixels.size(); i++)
    {
      y_temporaire = x * pixels.get(i).getX() + y;

      res = pixels.get(i).getY() - y_temporaire;

      residus.add((int)res);
    }

    return new Point(x, y);
  }
}

class ImageVectorisee extends ImageFiltree
{
  protected ArrayList<Point> pixels = new ArrayList<Point>();
  protected ArrayList<SegmentSVG> elements = new ArrayList<SegmentSVG>();

  protected ArrayList<Point>[] sous_segments = new ArrayList[5];

  private int premier_a;
  private int premier_b;

  public ImageVectorisee(String s)
  {
    super(s);

    int k;

    for(k = 0; k < 5; k++)
    {
      sous_segments[k] = new ArrayList();
    }
  }

  public ImageVectorisee(BufferedImage b)
  {
    super(b);

    int k;

    for(k = 0; k < 5; k++)
    {
      sous_segments[k] = new ArrayList();
    }
  }

  public void vectorisation_v1()
  {
    int i, j, k;

    for(i = 0; i < largeur; i++)
    {
      for(j = 0; j < hauteur; j++)
      {
        // point détecté = on lance une recherche des voisins
        if(image.getRGB(i, j) == Color.WHITE.getRGB())
        {
          pixels.clear(); // on vide le tableau

          pixels.add(new Point(i, j)); // on l'ajoute
          image.setRGB(i, j, Color.BLACK.getRGB()); // on l'efface de l'image

          // évaluation paresseuse
          if((i + 1 < largeur) && image.getRGB(i + 1, j) == Color.WHITE.getRGB())
          {
            recherche_ligne_horizontale(i + 1, j);
          }
          else if((j + 1 < hauteur) && image.getRGB(i, j + 1) == Color.WHITE.getRGB())
          {
            recherche_ligne_verticale(i, j + 1);
          }
          else if((i - 1 > 0) && (j + 1 < hauteur) && image.getRGB(i - 1, j + 1) == Color.WHITE.getRGB())
          {
            recherche_diagonale_gauche(i - 1, j + 1);
          }
          else if((i + 1 < largeur) && (j + 1 < hauteur) && image.getRGB(i + 1, j + 1) == Color.WHITE.getRGB())
          {
            recherche_diagonale_droite(i + 1, j + 1);
          }
          else
          {
            continue;
          }

          elements.add(new SegmentSVG(pixels.get(0), pixels.get(pixels.size() - 1)));
        }
      }
    }

    exportEnSVG();
  }

  private void recherche_ligne_horizontale(int i, int j)
  {
    pixels.add(new Point(i, j)); // on l'ajoute
    image.setRGB(i, j, Color.BLACK.getRGB()); // on l'efface de l'image

    if((i + 1 < largeur) && image.getRGB(i + 1, j) == Color.WHITE.getRGB())
    {
      recherche_ligne_horizontale(i + 1, j);
    }
  }

  private void recherche_ligne_verticale(int i, int j)
  {
    pixels.add(new Point(i, j)); // on l'ajoute
    image.setRGB(i, j, Color.BLACK.getRGB()); // on l'efface de l'image

    if((j + 1 < hauteur) && image.getRGB(i, j + 1) == Color.WHITE.getRGB())
    {
      recherche_ligne_verticale(i, j + 1);
    }
  }

  private void recherche_diagonale_gauche(int i, int j)
  {
    pixels.add(new Point(i, j)); // on l'ajoute
    image.setRGB(i, j, Color.BLACK.getRGB()); // on l'efface de l'image

    if((i - 1 > 0) && (j + 1 < hauteur) && image.getRGB(i - 1, j + 1) == Color.WHITE.getRGB())
    {
      recherche_diagonale_gauche(i - 1, j + 1);
    }
  }

  private void recherche_diagonale_droite(int i, int j)
  {
    pixels.add(new Point(i, j)); // on l'ajoute
    image.setRGB(i, j, Color.BLACK.getRGB()); // on l'efface de l'image

    if((i + 1 < largeur) && (j + 1 < hauteur) && image.getRGB(i + 1, j + 1) == Color.WHITE.getRGB())
    {
      recherche_diagonale_droite(i + 1, j + 1);
    }
  }

  /*


  */
  public void vectorisation_v2(double seuil)
  {
    int i, j, k;

    Point premiere_droite;

    for(i = 0; i < largeur; i++)
    {
      for(j = 0; j < hauteur; j++)
      {
        // point détecté = on lance une recherche des voisins
        if(image.getRGB(i, j) == Color.WHITE.getRGB())
        {
          // on initialise les 5 sous segments et on ajoute le point détecté
          for(k = 0; k < 5; k++)
          {
            sous_segments[k].clear();
            sous_segments[k].add(new Point(i, j));
          }

          image.setRGB(i, j, Color.BLACK.getRGB()); // on efface de l'image le point détecté

          // s'il y a un point en haut à droite
          // on utilise l'évaluation paresseuse pour vérifier qu'on ne dépasse pas les bords
          if((i + 1 < largeur) && (j - 1 >= 0) && image.getRGB(i + 1, j - 1) == Color.WHITE.getRGB())
          {
            sous_segments[0].add(new Point(i + 1, j - 1)); // on ajoute ce nouveau point et on l'efface
            image.setRGB(i + 1, j - 1, Color.BLACK.getRGB());

            recherche_droite(i + 1, j - 1, DroiteSVG.moindre_carre(sous_segments[0]), seuil, 0); // on lance une recherche de droite
            insertion_points(sous_segments[0]); // on réinsère les points pour les recherches suivantes
          }

          // s'il y a un point à droite
          if((i + 1 < largeur) && image.getRGB(i + 1, j) == Color.WHITE.getRGB())
          {
            sous_segments[1].add(new Point(i + 1, j));
            image.setRGB(i + 1, j, Color.BLACK.getRGB());

            recherche_droite(i + 1, j, DroiteSVG.moindre_carre(sous_segments[1]), seuil, 1);
            insertion_points(sous_segments[1]);
          }

          // s'il y a un point en bas
          if((j + 1 < hauteur) && image.getRGB(i, j + 1) == Color.WHITE.getRGB())
          {
            sous_segments[2].add(new Point(i, j + 1));
            image.setRGB(i, j + 1, Color.BLACK.getRGB());

            recherche_droite(i, j + 1, DroiteSVG.moindre_carre(sous_segments[2]), seuil, 2);
            insertion_points(sous_segments[2]);
          }

          // s'il y a un point en bas à gauche
          if((i - 1 >= 0) && (j + 1 < hauteur) && image.getRGB(i - 1, j + 1) == Color.WHITE.getRGB())
          {
            sous_segments[3].add(new Point(i - 1, j + 1));
            image.setRGB(i - 1, j + 1, Color.BLACK.getRGB());

            recherche_droite(i - 1, j + 1, DroiteSVG.moindre_carre(sous_segments[3]), seuil, 3);
            insertion_points(sous_segments[3]);
          }

          // s'il y a un point en bas à droite
          if((i + 1 < largeur) && (j + 1 < hauteur) && image.getRGB(i + 1, j + 1) == Color.WHITE.getRGB())
          {
            sous_segments[4].add(new Point(i + 1, j + 1));
            image.setRGB(i + 1, j + 1, Color.BLACK.getRGB());

            recherche_droite(i + 1, j + 1, DroiteSVG.moindre_carre(sous_segments[4]), seuil, 4);
            insertion_points(sous_segments[4]);
          }

          // on sélectionne le sous segment le plus grand
          int max_id = -1, max_size = Integer.MIN_VALUE;

          for(k = 0; k < 5; k++)
          {
            if(sous_segments[k].size() > max_size)
            {
              max_id = k;
              max_size = sous_segments[k].size();
            }
          }

          // on vérifie qu'un segment a bien été sélectionné
          if(max_id == -1)
            continue;

          // on efface les points de ce segment sur l'image
          suppression_point(sous_segments[max_id]);

          // on ajoute le segment dans notre liste de segments
          elements.add(new DroiteSVG(sous_segments[max_id].get(0), sous_segments[max_id].get(sous_segments[max_id].size() - 1), DroiteSVG.moindre_carre(sous_segments[max_id])));
        }
      }
    }

    exportEnSVG();
  }

  /*
  Récupère la 1ère droite "acceptable" selon le seuil et la sauvegarde dans l'attribut de classe sous_segments[i_sous_segment]
  */
  private void recherche_droite(int i, int j, Point premiere_droite, double seuil, int i_sous_segment)
  {
    Point droite;

    ArrayList<Point> arr = sous_segments[i_sous_segment];

    // s'il y a un point en haut à gauche
    if((i + 1 < largeur) && (j - 1 >= 0) && image.getRGB(i + 1, j - 1) == Color.WHITE.getRGB())
    {
      // on ajoute ce point
      arr.add(new Point(i + 1, j - 1));

      // on vérifie si la nouvelle droite ne dévie pas plus que le seuil par rapport à la 1ère
      droite = DroiteSVG.moindre_carre(arr);

      if(Math.abs(premiere_droite.getX() - droite.getX()) < seuil)
      {
        // on accepte la modification et on continue dans cette direction
        image.setRGB(i + 1, j - 1, Color.BLACK.getRGB());
        recherche_droite(i + 1, j - 1, premiere_droite, seuil, i_sous_segment);
        return;
      }
      else
      {
        // on refuse la modification et on change potentiellement de direction
        arr.remove(arr.size() - 1);
      }
    }

    // s'il y a un point à droite
    if((i + 1 < largeur) && image.getRGB(i + 1, j) == Color.WHITE.getRGB())
    {
      arr.add(new Point(i + 1, j));

      droite = DroiteSVG.moindre_carre(arr);

      if(Math.abs(premiere_droite.getX() - droite.getX()) < seuil)
      {
        image.setRGB(i + 1, j, Color.BLACK.getRGB());
        recherche_droite(i + 1, j, premiere_droite, seuil, i_sous_segment);
        return;
      }
      else
      {
        arr.remove(arr.size() - 1);
      }
    }

    // s'il y a un point en bas
    if((j + 1 < hauteur) && image.getRGB(i, j + 1) == Color.WHITE.getRGB())
    {
      arr.add(new Point(i, j + 1));

      droite = DroiteSVG.moindre_carre(arr);

      if(Math.abs(premiere_droite.getX() - droite.getX()) < seuil)
      {
        image.setRGB(i, j + 1, Color.BLACK.getRGB());
        recherche_droite(i, j + 1, premiere_droite, seuil, i_sous_segment);
        return;
      }
      else
      {
        arr.remove(arr.size() - 1);
      }
    }

    // s'il y a un pont en bas à gauche
    if((i - 1 > 0) && (j + 1 < hauteur) && image.getRGB(i - 1, j + 1) == Color.WHITE.getRGB())
    {
      arr.add(new Point(i - 1, j + 1));

      droite = DroiteSVG.moindre_carre(arr);

      if(Math.abs(premiere_droite.getX() - droite.getX()) < seuil)
      {
        image.setRGB(i - 1, j + 1, Color.BLACK.getRGB());
        recherche_droite(i - 1, j + 1, premiere_droite, seuil, i_sous_segment);
        return;
      }
      else
      {
        arr.remove(arr.size() - 1);
      }
    }

    // s'il y a un point en bas à droite
    if((i + 1 < largeur) && (j + 1 < hauteur) && image.getRGB(i + 1, j + 1) == Color.WHITE.getRGB())
    {
      arr.add(new Point(i + 1, j + 1));

      droite = DroiteSVG.moindre_carre(arr);

      if(Math.abs(premiere_droite.getX() - droite.getX()) < seuil)
      {
        image.setRGB(i + 1, j + 1, Color.BLACK.getRGB());
        recherche_droite(i + 1, j + 1, premiere_droite, seuil, i_sous_segment);
        return;
      }
      else
      {
        arr.remove(arr.size() - 1);
      }
    }

    // on s'arrête si les 5 possibilités ont été étudiées et ne sont pas satisfaisantes
  }

  private void insertion_points(ArrayList<Point> arr)
  {
    int i;

    // on insère un point blanc pour chaque point du tableau sauf le 1er
    for(i = 1; i < arr.size(); i++)
      image.setRGB((int)arr.get(i).getX(), (int)arr.get(i).getY(), Color.WHITE.getRGB());
  }

  private void suppression_point(ArrayList<Point> arr)
  {
    int i;

    // on insère un point noir pour chaque point du tableau sauf le 1er
    for(i = 1; i < arr.size(); i++)
      image.setRGB((int)arr.get(i).getX(), (int)arr.get(i).getY(), Color.BLACK.getRGB());
  }

  private void exportEnSVG()
  {
    try {

    	File f = new File("export.svg");

      String header = "<?xml version=\"1.0\" encoding=\"utf-8\"?><svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.2\" width=\"" + largeur + "\" height=\"" + hauteur + "\"><title>Fichier SVG</title>";

      // fond blanc à l'aide d'un rectangle de la taille de l'image
      String background = "<rect width=\"" + largeur + "\" height=\"" + largeur + "\" x=\"0\" y=\"0\" fill=\"black\"/>";

      String footer = "</svg>";

      int i;

      if (!f.exists())
      {
    	  f.createNewFile();
    	}

      FileWriter fw = new FileWriter(f.getAbsoluteFile());
    	BufferedWriter bw = new BufferedWriter(fw);

      // écriture du fichier
    	bw.write(header);
      bw.write(background);
      for(i = 0; i < elements.size(); i++)
      {
        bw.write(elements.get(i).toString());
      }
      bw.write(footer);

    	bw.close();
    } catch (IOException e) {
			e.printStackTrace();
		}
  }
}
