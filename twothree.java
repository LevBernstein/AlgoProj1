import java.io.*;
import java.util.*;

/**
 * Basic Algorithms Programming Assignment 1: Ford's Dilemma 1
 * @author Lev Bernstein
 * Provided code: Node, InternalNode, LeafNode, TwoThreeTree, Workspace, insert, doInsert, copyOutChildren, insertNode, resetGuide, resetChildren
 */


class Node {
    String guide;
    // guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
    Node child0, child1, child2;
    // child0 and child1 are always non-null
    // child2 is null iff node has only 2 children
}

class LeafNode extends Node {
    // guide points to the key
    int value;
}

class TwoThreeTree {
    Node root;
    int height;
    
    TwoThreeTree() {
        root = null;
        height = -1;
        // empty tree has a height of -1; tree with just root has a height of 0, as the root is 0 away from the leaves
    }
}
    
class WorkSpace {
    // this class is used to hold return values for the recursive doInsert routine (see below)
    Node newNode;
    int offset;
    boolean guideChanged;
    Node[] scratch;
}
    
public class twothree {
    
    public static void main(String[] args) throws Exception { // any method using BufferedWriter must be wrapped with throws Exception
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
        // note: if x > y, call printRange on y, x
        // if you printRange("a", "z"), it will look for everything alphabetically from a to z. Even if there aren't leaves exactly equivalent to a or z, it will print everything in that range.
        // use s1.compareTo(s2)
        int numPlanets;
        String key;
        int value;
        int routes;
        String[] starts;
        String[] ends;
        String[] buff;
        TwoThreeTree tree = new TwoThreeTree();
        Scanner scan = new Scanner(System.in);
        numPlanets = scan.nextInt();
        scan.nextLine();
        for (int i = 0; i < numPlanets; i++) {
            buff = scan.nextLine().split(" ");
            key = buff[0];
            value = Integer.parseInt(buff[1]);
            insert(key, value, tree);
            output.write("Inserting node with key " + key + " and value " + String.valueOf(value) + ".\n");
        }
        routes = scan.nextInt();
        scan.nextLine();
        starts = new String[routes];
        ends = new String[routes];
        for (int i = 0; i < routes; i++) {
            buff = scan.nextLine().split(" ");
            starts[i] = buff[0];
            ends[i] = buff[1];
        }
        
        for (int i = 0; i < routes; i++) {
            if (starts[i].compareTo(ends[i]) <= 0) { // if x <= y
                output.write("x < y\n");
                printRange(tree.root, starts[i], ends[i], tree.height, "", output); // we need to start with lo = -infinity. The smallest possible string for compareTo in Java is the empty string ""
            }
            else {
                output.write("x > y\n");
                printRange(tree.root, ends[i], starts[i], tree.height, "", output);
            }
        }
        
        output.flush(); // flush system.out at the end of main
    }
    
    static void printRange(Node p, String x, String y, int h, String lo, BufferedWriter output) throws Exception {
        output.write("Starting print.\n");
        if (h == 0) {
            if (p.guide.compareTo(x) >= 0 && p.guide.compareTo(y) <= 0) { // if p's guide is between x and y, inclusive, then p is a leaf node
                output.write(p.guide + " " + String.valueOf(p.value) + "\n");
                return;
            }
        }
        
        if (y.compareTo(lo) <= 0)
            return;
        String hi = p.guide;
        if (hi.compareTo(x) < 0)
            return;
        
        printRange(p.child0, x, y, h - 1, lo);
        printRange(p.child1, x, y, h - 1, p.child0.guide);
        if (p.child2 != null)
            printRange(p.child2, x, y, h - 1, p.child1.guide);
    }
    
    static void insert(String key, int value, TwoThreeTree tree) {
        // insert a key value pair into tree (overwrite existing value if key is already present)
        
        int h = tree.height;
        
        if (h == -1) { //for an empty tree:
            LeafNode newLeaf = new LeafNode();
            newLeaf.guide = key;
            newLeaf.value = value;
            tree.root = newLeaf; 
            tree.height = 0;
        }
            
        else {
            WorkSpace ws = doInsert(key, value, tree.root, h);
            
            if (ws != null && ws.newNode != null) {
                // create a new root
                
                InternalNode newRoot = new InternalNode();
                if (ws.offset == 0) {
                    newRoot.child0 = ws.newNode; 
                    newRoot.child1 = tree.root;
                }
                else {
                    newRoot.child0 = tree.root; 
                    newRoot.child1 = ws.newNode;
                }
                
                resetGuide(newRoot);
                tree.root = newRoot;
                tree.height = h+1;
            }
        }
    }
                
    static WorkSpace doInsert(String key, int value, Node p, int h) {
        // auxiliary recursive routine for insert
        
        if (h == 0) {
            // we're at the leaf level, so compare and either update value or insert new leaf
            
            LeafNode leaf = (LeafNode) p; //downcast
            int cmp = key.compareTo(leaf.guide);
            
            if (cmp == 0) {
                leaf.value = value; 
                return null;
            }
            
            // create new leaf node and insert into tree
            LeafNode newLeaf = new LeafNode();
            newLeaf.guide = key; 
            newLeaf.value = value;
            
            int offset = (cmp < 0) ? 0 : 1;
            // offset == 0 => newLeaf inserted as left sibling
            // offset == 1 => newLeaf inserted as right sibling
            
            WorkSpace ws = new WorkSpace();
            ws.newNode = newLeaf;
            ws.offset = offset;
            ws.scratch = new Node[4];
            
            return ws;
        }
        else {
            InternalNode q = (InternalNode) p; // downcast
            int pos;
            WorkSpace ws;
            
            if (key.compareTo(q.child0.guide) <= 0) {
                pos = 0; 
                ws = doInsert(key, value, q.child0, h-1);
            }
            else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
                pos = 1;
                ws = doInsert(key, value, q.child1, h-1);
            }
            else {
                pos = 2; 
                ws = doInsert(key, value, q.child2, h-1);
            }
            
            if (ws != null) {
                if (ws.newNode != null) {
                    // make ws.newNode child # pos + ws.offset of q
                    
                    int sz = copyOutChildren(q, ws.scratch);
                    insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
                    if (sz == 2) {
                        ws.newNode = null;
                        ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
                    }
                    else {
                        ws.newNode = new InternalNode();
                        ws.offset = 1;
                        resetChildren(q, ws.scratch, 0, 2);
                        resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
                    }
                }
                else if (ws.guideChanged) {
                    ws.guideChanged = resetGuide(q);
                }
            }
                    
            return ws;
        }
    }
                                    
                                    
    static int copyOutChildren(InternalNode q, Node[] x) {
        // copy children of q into x, and return # of children
        
        int sz = 2;
        x[0] = q.child0; x[1] = q.child1;
        if (q.child2 != null) {
            x[2] = q.child2; 
            sz = 3;
        }
        return sz;
    }
                                        
    static void insertNode(Node[] x, Node p, int sz, int pos) {
        // insert p in x[0..sz) at position pos,
        // moving existing extries to the right
        
        for (int i = sz; i > pos; i--)
            x[i] = x[i-1];
        
        x[pos] = p;
    }
                                        
    static boolean resetGuide(InternalNode q) {
        // reset q.guide, and return true if it changes.
        
        String oldGuide = q.guide;
        if (q.child2 != null)
            q.guide = q.child2.guide;
        else
            q.guide = q.child1.guide;
        
        return q.guide != oldGuide;
    }
                                        
                                        
    static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
        // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
        // also resets guide, and returns the result of that
        
        q.child0 = x[pos]; 
        q.child1 = x[pos+1];
        
        if (sz == 3) 
            q.child2 = x[pos+2];
        else
            q.child2 = null;
        
        return resetGuide(q);
    }
}
