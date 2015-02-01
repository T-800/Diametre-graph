import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Main {

    static class Sommet{
        public static final ArrayDeque<Sommet> file = new ArrayDeque<Sommet>();
        private static int cpt = 0;
        private int id;
        private int couleur;
        private int distance;
        private ArrayList<Sommet> listFrere;
        public ArrayList<Sommet> pere;

        public Sommet(){
            this.id = cpt;
            cpt++;
            this.couleur = 0;
            this.distance = 0;
            listFrere = new ArrayList<Sommet>();
            this.pere = new ArrayList<Sommet>();

        }

        public void ajouterConnexion(Sommet s){
            this.listFrere.add(s);
            s.getListFrere().add(this);
        }

        public ArrayList<Sommet> getListFrere() {
            return listFrere;
        }

        public int parcourLargeur(){
            this.couleur = 1;
            file.add(this);
            Sommet tmp = null;
            while (!file.isEmpty()){
                tmp = file.pop();
                for (Sommet s: tmp.getListFrere()){
                    if(s.couleur == 0){
                        s.distance = tmp.distance + 1;
                        file.add(s);
                        s.couleur = 1;
                    }
                }
                tmp.couleur = 2;
            }
            return tmp.distance;
        }

        public Sommet parcourLargeur_max(){
            this.couleur = 1;
            file.add(this);
            Sommet tmp = null, max = this;
            while (!file.isEmpty()){
                tmp = file.pop();
                for (Sommet s: tmp.getListFrere()){
                    if(s.couleur == 0){
                        s.distance = tmp.distance + 1;
                        if(s.distance > max.distance){
                            max = s;
                        }else if(s.distance == max.distance && s.id < max.id){
                            max = s;
                        }
                        file.add(s);
                        s.couleur = 1;
                    }
                }
                tmp.couleur = 2;
            }
            return max;
        }

        public Sommet parcourLargeur_mid(){
            this.couleur = 1;
            file.add(this);
            Sommet tmp, max = this;
            while (!file.isEmpty()){
                tmp = file.pop();
                for (Sommet s: tmp.getListFrere()){
                    if(s.couleur == 0){
                        s.pere.addAll(tmp.pere);
                        s.pere.add(tmp);
                        s.distance = tmp.distance + 1;
                        if(s.distance > max.distance){
                            max = s;
                        }else if(s.distance == max.distance && s.id < max.id){
                            max = s;
                        }
                        file.add(s);
                        s.couleur = 1;
                    }
                }
                tmp.couleur = 2;
            }
            return max;
        }

    }

    static class Graph{

        public Sommet[] listeSommets;

        public Graph(int taille){
            listeSommets = new Sommet[taille];
            for (int i = 0; i<taille; i++){
                listeSommets[i] = new Sommet();
            }
        }

        public void addSommet(String s){
            String [] ss = s.split(" ");
            listeSommets[Integer.parseInt(ss[0])-1].ajouterConnexion(listeSommets[Integer.parseInt(ss[1])-1]);

        }

        public void raz(){
            for (Sommet s : this.listeSommets){
                s.distance = 0;
                s.couleur = 0;
                s.pere = new ArrayList<Sommet>();
            }
        }

        public int algoExact(){
            int diametre= 0;
            int tmp;
            for (Sommet s : this.listeSommets){
                tmp = s.parcourLargeur();
                diametre = (tmp > diametre)?tmp:diametre;
                raz();
            }
            return diametre;
        }

        public int algoApproche(){
            Sommet s = listeSommets[0];
            s = s.parcourLargeur_max();
            raz();

            s = s.parcourLargeur_mid();

            s = s.pere.get(s.pere.size()/2);
            raz();

            s = s.parcourLargeur_max();
            raz();

            return s.parcourLargeur();
        }



    }


    public static void main(String [] args){

        BufferedReader reader = null;
        getArgs(args);
        try {
            reader = new BufferedReader(new FileReader(args[1]));

        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            System.exit(-1);
        }
        String s = "";
        Graph graph = null;
        try {
            s = reader.readLine();
            graph = new Graph(Integer.parseInt(s));
            while ((s = reader.readLine()) != null) {
                graph.addSommet(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("azertyuiop");
            System.exit(-1);
        }

        /*for (Sommet ss : graph.listeSommets){
            ss.printfrere();
        }*/
        if (args[0].equals("e")){
            System.out.println(graph.algoExact());
        }
        else System.out.println(graph.algoApproche());


    }


    public static void getArgs(String[] args){
        if(args.length != 2){
            System.err.println("Nombre d'argument ");
            System.exit(-1);
        }

        if(! args[0].equalsIgnoreCase("e") && ! args[0].equalsIgnoreCase("h"))
        {
            System.err.println("Argument 1 e ou h");
            System.exit(-2);
        }
        if (!args[1].endsWith(".pfg")){
            System.err.println("Argument 2 Fichier .pfg");
            System.exit(-3);
        }

    }
}
