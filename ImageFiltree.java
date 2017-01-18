import java.io.* ;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;

class ImageFiltree
{
  protected BufferedImage image;

  protected int largeur;
  protected int hauteur;

  public ImageFiltree(String s)
  {
    File f = new File(s);

    try
    {
      image = ImageIO.read(f);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

    largeur = image.getWidth();
    hauteur = image.getHeight();
  }

  public ImageFiltree(BufferedImage b)
  {
    image = b;

    largeur = b.getWidth();
    hauteur = b.getHeight();
  }

  public BufferedImage getImage()
  {
    return image;
  }

  /*
    Renvoie l'image en niveau de gris
  */
  public BufferedImage filtreGris()
  {
    int i, j;

    BufferedImage res = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);

    for(i = 0; i < largeur; i++)
    {
      for(j = 0; j < hauteur; j++)
      {
         // on utilise des constantes donnant un bon niveau de gris
        Color c = new Color(image.getRGB(i, j));
        int r = (int)(c.getRed() * 0.21);
        int g = (int)(c.getGreen() * 0.72);
        int b = (int)(c.getBlue() * 0.07);

        int rgb = r + g + b;

        Color c2 = new Color(rgb, rgb, rgb);

        res.setRGB(i, j, c2.getRGB());
      }
    }

    return res;
  }

  /*
    Renvoie l'image en noir et blanc selon le seuil passé en paramètre
  */
  public BufferedImage seuil(int seuil)
  {
    int i, j;

    BufferedImage res = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);

    for(i = 0; i < largeur; i++)
    {
      for(j = 0; j < hauteur; j++)
      {
        Color c = new Color(image.getRGB(i, j));

        int r = c.getRed();

        if(r < seuil)
          res.setRGB(i, j, Color.BLACK.getRGB());
        else
          res.setRGB(i, j, Color.WHITE.getRGB());
      }
    }

    return res;
  }

  /*
    Renvoie les contours de l'image avec un filtre Sobel
  */
  public BufferedImage filtreSobel()
  {
    BufferedImage imageResultat = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);

    int i, j, res, x, y;

    // on évite la bordure en commençant à 1 et en s'arrêtant à - 1
    for(i = 1; i < largeur - 1; i++)
    {
      for(j = 1; j < hauteur - 1; j++)
      {
        Matrice m = new Matrice(image, i, j);

        // on détermine avec ces opérations le résultat
        x = Matrice.convolution(m, Matrice.mSobelX);
        y = Matrice.convolution(m, Matrice.mSobelY);

        res = (int)(Math.sqrt(x * x + y * y) % 255);

        Color cres = new Color(res, res, res);

        imageResultat.setRGB(i, j, cres.getRGB());
      }
    }

    return imageResultat;
  }

  /*
    Renvoie les contours de l'image avec un filtre Prewitt
  */
  public BufferedImage filtrePrewitt()
  {
    BufferedImage imageResultat = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);

    int i, j, res, x, y;

    // on évite la bordure en commençant à 1 et en s'arrêtant à - 1
    for(i = 1; i < largeur - 1; i++)
    {
      for(j = 1; j < hauteur - 1; j++)
      {
        Matrice m = new Matrice(image, i, j);

        x = Matrice.convolution(m, Matrice.mPrewittX);
        y = Matrice.convolution(m, Matrice.mPrewittY);

        res = (int)(Math.sqrt(x * x + y * y) % 255);

        Color cres = new Color(res, res, res);

        imageResultat.setRGB(i, j, cres.getRGB());
      }
    }

    return imageResultat;
  }
}
