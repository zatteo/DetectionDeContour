import java.awt.Color;
import java.awt.image.BufferedImage;

class Matrice{
  protected int largeur;
  protected int hauteur;
  protected int[][] matrice;

  // gradients statiques pour Sobel
  public static final Matrice mSobelX = new Matrice(-1, 0, 1, -2, 0, 2, -1, 0, 1);
  public static final Matrice mSobelY = new Matrice(-1, -2, -1, 0, 0, 0, 1, 2, 1);

  // gradients statiques pour Prewitt
  public static final Matrice mPrewittX = new Matrice(-1, 0, 1, -1, 0, 1, -1, 0, 1);
  public static final Matrice mPrewittY = new Matrice(-1, -1, -1, 0, 0, 0, 1, 1, 1);

  // constructeur depuis une série de 9 entiers - matrice 3x3 seulement
  public Matrice(int...args) {
    largeur = 3;
    hauteur = 3;

    matrice = new int[3][3];

    matrice[0][0] = args[0];
    matrice[0][1] = args[1];
    matrice[0][2] = args[2];
    matrice[1][0] = args[3];
    matrice[1][1] = args[4];
    matrice[1][2] = args[5];
    matrice[2][0] = args[6];
    matrice[2][1] = args[7];
    matrice[2][2] = args[8];
  }

  // constructeur depuis un pixel d'une image - matrice 3x3 seulement
  public Matrice(BufferedImage image, int i, int j){
    largeur = 3;
    hauteur = 3;

    matrice = new int[3][3];

    Color c0 = new Color(image.getRGB(i - 1, j - 1));
    Color c1 = new Color(image.getRGB(i, j - 1));
    Color c2 = new Color(image.getRGB(i + 1, j - 1));
    Color c3 = new Color(image.getRGB(i - 1, j));
    Color c4 = new Color(image.getRGB(i, j));
    Color c5 = new Color(image.getRGB(i + 1, j));
    Color c6 = new Color(image.getRGB(i - 1, j + 1));
    Color c7 = new Color(image.getRGB(i, j + 1));
    Color c8 = new Color(image.getRGB(i + 1, j + 1));

    matrice[0][0] = c0.getRed();
    matrice[0][1] = c1.getRed();
    matrice[0][2] = c2.getRed();
    matrice[1][0] = c3.getRed();
    matrice[1][1] = c4.getRed();
    matrice[1][2] = c5.getRed();
    matrice[2][0] = c6.getRed();
    matrice[2][1] = c7.getRed();
    matrice[2][2] = c8.getRed();
  }

  public int getIJ(int i, int j){
    return matrice[i][j];
  }

  public void affichage(){
    int i, j;
    for(i = 0; i < largeur; i++){
      for(j = 0; j < hauteur; j++){
        System.out.print(matrice[i][j] + " ");
      }
      System.out.println("");
    }
  }

  public static int convolution(Matrice m1, Matrice m2) {
    if(m1.largeur != 3 || m1.hauteur != 3) {
      return 0;
    }
    int i, j, res = 0;

    for(i = 0; i < 3; i++){
      for(j = 0; j < 3; j++){
        res += m1.getIJ(i, j) * m2.getIJ(i, j);
      }
    }
    return res;
  }
}
