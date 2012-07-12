/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdt.tree.wordtree.policy;

import collect.HashTree;
import collect.Node;
import collect.Tree;
import collect.UnorderedNode;
import crdt.Factory;
import crdt.set.CRDTSet;
import crdt.set.SetOperation;
import crdt.tree.wordtree.WordPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * une idée, si ça peut aider. C'est ce que j'ai essayé de t'expliquer avant de partir
 * Il faut l'adapter à ta modélisation que j'ai lu un peut en diagonal.
 * Je n'ai pas vraiment plus simple.
 * 
 * @author Stephane
 */
public class MyWordIncremetanlTrapear<T>  extends WordPolicy<T> implements Factory<WordPolicy<T>>, Observer {

    HashMap<List<T>, NodeContainer> map; /* Fait la liaison entre une liste de mot et un containeur de noeud*/
    HashTree tree; /* arbre du lookup */

    @Override
    public MyWordIncremetanlTrapear<T> create() {
        MyWordIncremetanlTrapear<T> wp = new MyWordIncremetanlTrapear();
        tree=new HashTree();
        return (MyWordIncremetanlTrapear<T>) wp;
    }

    @Override
   synchronized public void update(Observable o, Object o1) {
        if (o instanceof CRDTSet
                && o1 instanceof SetOperation) {
            SetOperation o2 = (SetOperation) o1;
            List<T> mot = (List<T>) o2.getContent();
            if (o2.getType() == SetOperation.OpType.add) {
                /*
                 * Ajout d'un mot: - Si le noeud n'existe pas on le crée avec
                 * son chemin. - on le démarque si était ghost
                 */
                NodeContainer n;
                n = getNode(mot);
                /*
                 * le noeud n'est pas ghost
                 */
                n.setGhost(false);


            } else {
                /*
                 * Suppression d'un noeud : 
                 * - on le marque ghost et on lance  cleanPath avec ce noeud.
                 * 
                 */
                NodeContainer n = map.get(mot);
                /*
                 * Le mot doit être là par définition.
                 */
                n.setGhost(true);
                cleanPath(n);
            }
        }
    }
    /* Va cherche un noeud 
     * S'il n'existe pas il va le créer en cherchant le père et récurcivement.
     */
    NodeContainer getNode(List<T> mot) {
        if (mot.isEmpty()) /*mot vite n'a pas de correspondance */
            return null;
        NodeContainer node = map.get(mot);/* On récupère le noeud s'il existe */
        if (node==null){ /*Si n'existe pas  */ 
            /* on cherche sont père récurcivement */
            NodeContainer father=getNode(mot.subList(1, mot.size() - 2)); 
            /* On retourne nouveau noeud */
            return new NodeContainer(father,mot);
        }else{
            /* Sinon on retourne celui qu'on a trouvé */
            return node;
        }
    }

    void cleanPath(NodeContainer n) {
        /*
         * On vérifie que le noeud est ghost et que ce n'est pas la racine sinon on ne fait rien. 
         */
        if (n!=null && n.isGhost()) {
            /*
             * On vérifie qu'il n'y a pas de fils. -S'il a des fils cela veut
             * dire qu'il y a un autre chemin terminant par un non ghost.
             */
            if (n.getNode().getChildrenNumber() == 0) {
                /*
                 * On supprime le noeud
                 */
                n.del();
                cleanPath(n.getFather());/* On remonte */
            }
        }
    }
    @Override
    synchronized public Tree<T> lookup() {
        return tree;
    }

    @Override
    public Set<List<T>> addMapping(UnorderedNode<T> node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<List<T>> delMapping(UnorderedNode<T> node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    class NodeContainer {

        private Node<T> node;       /*
         * Le noeud du lookup
         */

        private boolean ghost;       /*
         * Savoir si le neoud est dans le CRDT
         */

        private NodeContainer father;/*
         * Pour le parcours
         */

        private List<T> word; /*
         * Histoire de pouvoir enlever la clef sans trop de prob
         */


         NodeContainer(NodeContainer father, List<T> word) {
            this.node = tree.add((father != null) ? father.getNode() : tree.getRoot(), word.get(word.size() - 1));
            this.ghost = true; /* Par défaut c'est un ghost */
            this.father = father;
            this.word = word;
            map.put(word,this);

        }
         /**
          * Supprime le noeud de la map et du lookup
          */
        void del(){
            tree.remove(this.getNode());
            map.remove(this.getWord());
        }
        
        /**
         * @return the ghost
         */
        boolean isGhost() {
            return ghost;
        }

        /**
         * @param ghost the ghost to set
         */
         void setGhost(boolean ghost) {
            this.ghost = ghost;
        }

        /**
         * @return the father
         */
         NodeContainer getFather() {
            return father;
        }

        /**
         * @return the word
         */
         List<T> getWord() {
            return word;
        }

        /**
         * @return the node
         */
         Node<T> getNode() {
            return node;
        }
    }
}
