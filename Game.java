import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends JFrame {
    boolean OyuncuKaybettiMi = false; // Oyuncu vurulur ise ture yap bitir
    boolean OyuncuKazandiMi = false; // Tum enemyler yenildi ise true yap bitir
    ReentrantLock lock = new ReentrantLock(true);
    int FriendNum =0;
    int EnemyNum =0;
    int squareSize = 10;
   ArrayList<oyunElemanlarininYerleriTupules> liste = new ArrayList<>();

    class oyunElemanlarininYerleriTupules{
        int x,y;
        String s;
        public oyunElemanlarininYerleriTupules(int x, int y,String s){
            this.x = x;
            this.y = y;
            this.s = s;
        }
    }
    boolean shouldFire = true; // if mouse is pressed set true. Count everytime you shoot.
    // If total enemy + friend = tot shots fired set to false.
    int fireCount = 0;
    public class Bullet{
        String s;
        String direction; // a or d
        int x,y;
        public Bullet(int x, int y, String s, String direction){
            this.x = x;
            this.y = y;
            this.s = s;
            this.direction =direction;
        }
    }
    ArrayList<Bullet> bullets = new ArrayList<>();
    public class Enemy extends Thread{ // siyah kare
        int max = 500;
        int min = 0;
        Random random = new Random();
        @Override
        public void run() {
            EnemyNum++;
            try {
                lock.lock();
                int y = 10 * random.nextInt(49) - squareSize;
                int x = 10 * random.nextInt(49) - squareSize;
                for (int n = 0; n < liste.size(); n++) { // ensure no duplicates at same location exist
                    if ((liste.get(n).x == x && liste.get(n).y == y) || (x<10)||y<30) {
                        x = 10 * random.nextInt(49) - squareSize;
                        y = 10 * random.nextInt(49) - squareSize;
                        n = -1; // to restart
                    }
                    else if (x== 250 && y==250) {
                        x = 10 * random.nextInt(49) - squareSize;
                        y = 10 * random.nextInt(49) - squareSize;
                        n = -1; // to restart
                    }
                }
                Graphics g = getGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(x, y, squareSize, squareSize);
                liste.add(new oyunElemanlarininYerleriTupules(x, y, "enemy"));

            } finally {
                lock.unlock();
            }
        }
    }
    public class Friend extends  Thread{ // yesil kare
        Random random = new Random();

        @Override
        public void run() {
            FriendNum++;
            try {
                lock.lock();
                int y = 10 * random.nextInt(49) - squareSize;
                int x = 10 * random.nextInt(49) - squareSize;
                for (int n = 0; n < liste.size(); n++) { // ensure no duplicates at same location exist
                    if (x == 250 && y == 250) {
                        x = 10 * random.nextInt(49) - squareSize;
                        y = 10 * random.nextInt(49) - squareSize;
                        n = -1; // to restart
                    }
                   else  if ((liste.get(n).x == x && liste.get(n).y == y) || (x<10)||y<30) {
                        x = 10 * random.nextInt(49) - squareSize;
                        y = 10 * random.nextInt(49) - squareSize;
                        n = -1; // to restart
                    }
                }
                Graphics g = getGraphics();
                g.setColor(Color.GREEN);
                g.fillRect(x, y, squareSize, squareSize);
                liste.add(new oyunElemanlarininYerleriTupules(x, y, "friend"));
            } finally {
            lock.unlock();
            }

        }
    }
    class bulletDegerlendirmeClassi extends Thread{
        @Override
        public void run() {
            while (true) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {

                }
                Arepaint();
                BulletlariDegerlendir();
            }
        }
    }
    boolean click = false;
    /*
    *  if(shouldFire == true){
                    fireCount++;
                    bullets.add(new Bullet(x,y,"friend","d"));
                    bullets.add(new Bullet(x,y,"friend","a"));
                } //if(fireCount == FriendNum+EnemyNum) {shouldFire = false; fireCount = 0;}*/
    public void BulletlariDegerlendir(){
        if(shouldFire = true){
                for(int n =0; n<liste.size(); n++){
                    oyunElemanlarininYerleriTupules b = liste.get(n);
                    if(shouldFire == true){
                        fireCount++;
                        if(b.s.equals("player") && click == true){
                            bullets.add(new Bullet(b.x,b.y,"player","d"));
                            bullets.add(new Bullet(b.x,b.y,"player","a"));
                            click = false;
                        } else if(onKereOldu == 10){
                                if(b.s.equals("friend")){
                            bullets.add(new Bullet(b.x,b.y,"friend","d"));
                            bullets.add(new Bullet(b.x,b.y,"friend","a"));
                            } else if(b.s.equals("enemy")){
                                bullets.add(new Bullet(b.x,b.y,"enemy","d"));
                                bullets.add(new Bullet(b.x,b.y,"enemy","a"));
                            }
                        } else {
                            break;
                        }
                    }//if(fireCount == FriendNum+EnemyNum) {shouldFire = false; fireCount = 0;}
            }
                if(onKereOldu == 10)
                    onKereOldu = 0;
                onKereOldu++;
        }
        for(int n=0;n<bullets.size() ;n++){
            Bullet bullet = bullets.get(n);
            if(bullet.direction.equals("a")){
                if(bullet.x>0){
                    bullet.x -= 10;
                } else{
                    bullets.remove(n);
                    n=-1;
                }
            }else if(bullet.direction.equals("d")){
                if(bullet.x<500){
                    bullet.x+=10;
                }else{
                    bullets.remove(n);
                    n=-1;
                }
            }
        }
        karelerVurulduMu();
    }
    int removedEnemyNum = 0;
    public void karelerVurulduMu(){ // her bir bullet yerini her bir kare yeri ile mukayese et
        for(int n=0;n<liste.size() ;n++){
            oyunElemanlarininYerleriTupules o = liste.get(n);
            for(int m=0;m<bullets.size() ;m++){
                Bullet b = bullets.get(m);
                if(b.x>=o.x&& b.x+5<=o.x+squareSize && b.y>=o.y && b.y< o.y+squareSize){
                    bullets.remove(m);
                    if(b.s.equals("friend")){
                        if(o.s.equals("enemy")) { m--;liste.remove(n); n--; removedEnemyNum++;}

                    }else if(b.s.equals("enemy")){
                        if(o.s.equals("friend")) {m--;liste.remove(n); n--;}
                        else if (o.s.equals("player")){m--;liste.remove(n); n--; PopUp(false);}
                    } else{
                        if(o.s.equals("enemy")) { m--; liste.remove(n); n--; removedEnemyNum++;}
                    }

                }
            }
        }

    }
    protected String[] karelerIcinYonBelirle(){
        String[] stringAr = new String[liste.size()-1];
        for(int n=1;n<liste.size() ;n++){ // ilki sınır kontrolu. kareler ust uste gelir ise silinsin
            String choices = "WASD"; // I will randomly pick 1 of 4 letters
            String res = ""+ choices.charAt((int) (Math.random()*4));
            oyunElemanlarininYerleriTupules c = liste.get(n);
            if (c.x == 10 && c.y ==30) {
                choices = "SD";
                 res = ""+ choices.charAt((int) (Math.random()*2));
            } else if (c.x == 10 && c.y==480) {
                choices = "WS";
                res = ""+ choices.charAt((int) (Math.random()*2));
            }  else if (c.y == 30 && c.x ==480) {
                choices = "SA";
                res = ""+ choices.charAt((int) (Math.random()*2));
            } else if (c.x == 480 && c.y==480) {
                choices = "WA";
                res = ""+ choices.charAt((int) (Math.random()*2));
            }else if (c.x == 10) {
                choices = "WSD";
                res = ""+ choices.charAt((int) (Math.random()*3));
            }else if (c.x == 480) {
                choices = "WSA";
                res = ""+ choices.charAt((int) (Math.random()*3));
            }else if (c.y == 30) {
                choices = "SAD";
                res = ""+ choices.charAt((int) (Math.random()*3));
            }else if (c.y == 480) {
                choices = "AWD";
                res = ""+ choices.charAt((int) (Math.random()*3));
            }
            stringAr[n-1] = res;

        }
        return stringAr;
    }
    public void KareleriOynat(String [] arr){
        boolean playereGeldi = false; // index olarak bakıyoruz
        for(int n=1;n<liste.size() ;n++){
            oyunElemanlarininYerleriTupules c = liste.get(n);
            if(c.s.equals("player")){ // act as if it doesnt count
            playereGeldi = true;
            }
            if(playereGeldi == false){
                oyunElemanlarininYerleriTupules changed = liste.get(n);
                giden = changed;
                if(arr[n-1].toLowerCase().equals("w") && gidilenYerBosIseTrue(changed.x, changed.y-10,changed.s)){
                    changed.y -=10;
                }else if(arr[n-1].toLowerCase().equals("s")&& gidilenYerBosIseTrue(changed.x, changed.y+10,changed.s)){
                    changed.y +=10;
                }else if(arr[n-1].toLowerCase().equals("a")&& gidilenYerBosIseTrue(changed.x-10, changed.y,changed.s)){
                    changed.x -=10;
                }else if(arr[n-1].toLowerCase().equals("d")&& gidilenYerBosIseTrue(changed.x+10, changed.y,changed.s)){
                    changed.x +=10;
                }
                liste.set(n,changed);
            } else if (playereGeldi == true){
                oyunElemanlarininYerleriTupules changed = liste.get(n);
                giden = changed;
                if(arr[n-1].toLowerCase().equals("w") && gidilenYerBosIseTrue(changed.x, changed.y-10,changed.s)){
                    changed.y -=10;
                }else if(arr[n-1].toLowerCase().equals("s")&& gidilenYerBosIseTrue(changed.x, changed.y+10,changed.s)){
                    changed.y +=10;
                }else if(arr[n-1].toLowerCase().equals("a")&& gidilenYerBosIseTrue(changed.x-10, changed.y,changed.s)){
                    changed.x -=10;
                }else if(arr[n-1].toLowerCase().equals("d")&& gidilenYerBosIseTrue(changed.x+10, changed.y,changed.s)){
                    changed.x +=10;
                }
                liste.set(n+1,changed);
            }
        }
    }
    public void kapa(){
        dispose();
    }
    public void PopUp(boolean bool){ // false ise kaybretti
        class ConfirmWindow extends JFrame implements WindowListener{
             public ConfirmWindow() {
                 setSize(200, 200);
                 setLayout(new BorderLayout());
                 JLabel outcome = new JLabel();
                 outcome.setText("Oyunu kazandınız");
                 if(bool == false) outcome.setText("Oyunu kaybettiniz");
                 setLayout(new BorderLayout());
                 add(outcome,BorderLayout.CENTER);
                 setVisible(true);
                 addWindowListener(this);
                 kapa();
             }
            @Override
            public void windowOpened(WindowEvent e) {
            }
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            @Override
            public void windowClosed(WindowEvent e) {
            }
            @Override
            public void windowIconified(WindowEvent e) {
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
            }
            @Override
            public void windowActivated(WindowEvent e) {
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        }
         new ConfirmWindow();

    }/*public class BulletElemanlariniCizen extends Thread{ // bitirme conditionuna bakar
        @Override
        public void run() {
            while(true){ // eleman sayısı azalmis ise
                if(getGraphics()!= null)
                    try{
                        sleep(100);
                        boolean playerExists = false;
                        boolean enemyExists = false;
                        for (oyunElemanlarininYerleriTupules o : liste) {
                            if (o.s.equals("player")) {
                                playerExists = true;
                            }
                            if (o.s.equals("enemy")) enemyExists = true;
                        }
                        if (playerExists == false) PopUp(false);
                        else if (enemyExists == false) PopUp(true);
                       // System.out.println("Yon belirlicem");
                        //System.out.println("Yon belirledim");
                        Arepaint();
                       // System.out.println("cizdim");
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println(e.toString());
                    }

            }
        }
    }*/
    int besKereOldu = 0;
    boolean playerExists = false;
    boolean enemyExists = false;
    public class OyunElemanlariniCizen extends Thread{ // bitirme conditionuna bakar
        @Override
        public void run() {
           while(true){ // eleman sayısı azalmis ise
               if(getGraphics()!= null)
                   try{
                       playerExists = false;
                       enemyExists = false;
                       sleep(100);
                           for (int n=0;n<liste.size() ;n++) {
                               oyunElemanlarininYerleriTupules o = liste.get(n);
                               if (o.s.equals("player")) {
                                   playerExists = true;
                               }
                               if (o.s.equals("enemy")) enemyExists = true;
                           }
                           if (playerExists == false) PopUp(false);
                           else if (enemyExists == false) PopUp(true);
                       //System.out.println("Yon belirlicem");
                       if(besKereOldu ==5){
                          // BulletlariDegerlendir();
                           KareleriOynat(karelerIcinYonBelirle()); besKereOldu = 0;}
                       besKereOldu++;
                       //System.out.println("Yon belirledim");
                        Arepaint();
                       //System.out.println("cizdim");
                   }catch (Exception e){

                   }

            }
        }
    }
    public class yonbelirlerim extends Thread{
        @Override
        public void run() {
            KareleriOynat(karelerIcinYonBelirle());
        }
    }

    oyunElemanlarininYerleriTupules giden= null;
    public boolean gidilenYerBosIseTrue(int x, int y, String taraf){
        boolean bos = true;

        for(int n=0;n<liste.size() ;n++){
            oyunElemanlarininYerleriTupules c = liste.get(n);
            if(c.x == x && c.y== y){
                bos = false;
                if(c.s.equals("enemy") && taraf.equals("enemy")){
                    bos= false;
                } else if (c.s.equals("enemy")){
                    if(taraf.equals("player")){
                        playerExists = false;
                        liste.remove(n);
                        PopUp(false);
                        for(int m=0;m<liste.size() ;m++){
                            if(liste.get(m)== giden) {liste.remove(m);}
                        }

                    }
                    // remove the friend and enemy
                    else{
                        liste.remove(n);
                        for(int m=0;m<liste.size() ;m++){
                            if(liste.get(m)== giden) {liste.remove(m); break;}
                        }
                    }
                }else if(c.s.equals("friend")){
                    if(taraf.equals("friend")|| taraf.equals("player")){
                        bos = false;
                    }
                    else{
                        liste.remove(n);
                        for(int m=0;m<liste.size() ;m++){
                            if(liste.get(m)== giden) {liste.remove(m); break;}
                        }
                    }
                }
            }
        }

        return  bos;
    }
    int onKereOldu = 0;
    public void Arepaint() { // repaints old screen
        Graphics g = getGraphics();
        super.paint(g);
        try {
            for (int n = 0; n < liste.size(); n++) {
                oyunElemanlarininYerleriTupules o = liste.get(n);
                if (o.s.equals("player")) {
                    g.setColor(Color.RED);
                    g.fillRect(o.x, o.y, squareSize, squareSize);
                } else if (o.s.equals("friend")) {
                    g.setColor(Color.GREEN);
                    g.fillRect(o.x, o.y, squareSize, squareSize);
                } else if (o.s.equals("enemy")) {
                    g.setColor(Color.BLACK);
                    g.fillRect(o.x, o.y, squareSize, squareSize);
                }
            }
            for (int n = 0; n < bullets.size(); n++) {
                Bullet bullet = bullets.get(n);
                if (bullet.s.equals("player")) {
                    g.setColor(Color.ORANGE);
                    g.fillRect(bullet.x, bullet.y, 5, 5);
                } else if (bullet.s.equals("friend")) {
                    g.setColor(Color.MAGENTA);
                    g.fillRect(bullet.x, bullet.y, 5, 5);
                } else {
                    g.setColor(Color.BLUE);
                    g.fillRect(bullet.x, bullet.y, 5, 5);
                }
            }
        } catch (Exception e){

        }
        }

    public class AirCraft extends  Thread{
        @Override
        public void run() {
            Graphics g = getGraphics();
            g.setColor(Color.RED);
            g.fillRect(250,250,squareSize,squareSize);

        }

    }
    class OyunPaneli extends JPanel implements MouseInputListener, KeyListener {
        @Override
        public void mouseClicked(MouseEvent e) {
        }
        @Override
        public void mousePressed(MouseEvent e) { // ates et
            click = true;
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        }
        @Override
        public void mouseEntered(MouseEvent e) {
        }
        @Override
        public void mouseExited(MouseEvent e) {
        }
        @Override
        public void mouseDragged(MouseEvent e) {
        }
        @Override
        public void mouseMoved(MouseEvent e) {
        }
        @Override
        public void keyTyped(KeyEvent e) {
        }
        @Override
        public void keyPressed(KeyEvent e) {
            for(int n=0;n<liste.size() ;n++) {
                lock.lock();
                oyunElemanlarininYerleriTupules o = liste.get(n);
                giden = o;
                if(o.s.equals("player")) {
                    if ((""+e.getKeyChar()).toLowerCase().equals("w") && o.y> 30 && gidilenYerBosIseTrue(o.x,o.y-10,"player")) {
                        o.y -= 10;
                    } else if ((""+e.getKeyChar()).toLowerCase().equals("s")&&o.y< 480&& gidilenYerBosIseTrue(o.x,o.y+10,"player")) {
                        o.y += 10;
                    } else if ((""+e.getKeyChar()).toLowerCase().equals("a") && o.x> 10&& gidilenYerBosIseTrue(o.x-10,o.y,"player")) {
                        o.x -= 10;
                    } else if ((""+e.getKeyChar()).toLowerCase().equals("d")&&o.x< 480&& gidilenYerBosIseTrue(o.x+10,o.y,"player")) {
                        o.x += 10;
                    }
                   // System.out.println(o.x + " y:" + o.y);
                }
            }lock.unlock();
        }
        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
    OyunPaneli oyunPaneli = new OyunPaneli();
    public Game(){
        //capı seyi kırk kaplıyor
        setLayout(new BorderLayout());
        setSize(500,500);
        oyunPaneli.setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(oyunPaneli,BorderLayout.CENTER);
        liste.add(new oyunElemanlarininYerleriTupules(250, 250, "player"));
        OyunElemanlariniCizen o = new OyunElemanlariniCizen();
        o.start();
        bulletDegerlendirmeClassi b = new bulletDegerlendirmeClassi();
        b.start();
       // BulletElemanlariniCizen b = new BulletElemanlariniCizen();
       // b.start();
        addKeyListener(oyunPaneli);
        addMouseListener(oyunPaneli);

       // OyunElemanlariniCizen o = new OyunElemanlariniCizen();
       // o.start();
        setVisible(true);
    }

}
