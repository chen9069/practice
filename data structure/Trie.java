class TrieNode {
    char val;
    boolean isLeaf;
    TrieNode[] children;
    public TrieNode() {
        children = new TrieNode[26];
    }
    public TrieNode(char val) {
        this.val = val;
        this.isLeaf = false;
        children = new TrieNode[26];
    }
}

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Inserts a word into the trie.
    public void insert(String word) {
        TrieNode tmp = root;
        for (char w : word.toCharArray()) {
            if (tmp.children[w-'a'] == null)
                tmp.children[w-'a'] = new TrieNode(w);
            tmp = tmp.children[w-'a'];
        }
        tmp.isLeaf = true;
    }

    // Returns if the word is in the trie.
    public boolean search(String word) {
        TrieNode tmp = root;
        for (char w : word.toCharArray()) {
            if (tmp.children[w-'a'] == null)
                return false;
            tmp = tmp.children[w-'a'];
        }
        return tmp.isLeaf;
    }

    // Returns if there is any word in the trie
    // that starts with the given prefix.
    public boolean startsWith(String prefix) {
        TrieNode tmp = root;
        for (char w : prefix.toCharArray()) {
            if (tmp.children[w-'a'] == null)
                return false;
            tmp = tmp.children[w-'a'];
        }
        return true;
    }
}
