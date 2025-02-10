package spider.neo.solution.flowcontrol;

import spider.neo.solution.flowcontrol.trie.BulkheadTrie;
import spider.neo.solution.flowcontrol.trie.RateLimiterTrie;

public class Test {
    public static void main(String[] args) {
        RateLimiterTrie trie = new RateLimiterTrie();

        trie.insert("/product/item/detail");
        trie.insert("/product/item/detail/more");
        trie.insert("/product/*");

        System.out.println("📌 Initial Trie:");
        trie.printTrie();
        System.out.println();

        System.out.println("🔍 Searching for: /product/item/detail");
        System.out.println(trie.search("/product/item/detail"));
        System.out.println();

        System.out.println("🔍 Searching for: /product/item/detail/more");
        System.out.println(trie.search("/product/item/detail/more"));
        System.out.println();

        System.out.println("🔍 Searching for: /product/item/detail/extra");
        System.out.println(trie.search("/product/item/detail/extra"));
        System.out.println();

        System.out.println("🔍 Searching for: /human/number/detail");
        String result = trie.search("/human/number/detail");
        System.out.println(result == null ? "null" : result);
        System.out.println();
    }
}
