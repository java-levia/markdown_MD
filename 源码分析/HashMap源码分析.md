HashMap源码分析

1. HashMap的整体结构

   - 总体上HashMap的结构是数组加链表（JDK1.8之后引入了二叉树），采用的是在数组中存二叉树的方式。每个键值对的信息封装在一个Node对象中作为二叉树的一个节点存储，Node中除了存有key/value属性外还有两个其他属性，其一是通过Key计算出来的hash值，其二是存储在同一个链表中的下一个node的信息。

   ```java
   
   
   final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                      boolean evict) {
       	//创建一个节点数组
           Node<K,V>[] tab; 
       	//创建一个节点
       	Node<K,V> p; 
       
       	int n, i;
       	//如果节点数组为空 使用resize()方法初始化数组的大小
           if ((tab = table) == null || (n = tab.length) == 0)
               n = (tab = resize()).length;
       	//通过 (n - 1) & hash 计算得出node在数组中的位置，如果这个位置为空，就将node存储在数组中
           if ((p = tab[i = (n - 1) & hash]) == null)
               tab[i] = newNode(hash, key, value, null);
           else {
               //如果位置不为空
               
               Node<K,V> e;
               K k;
               
               if (p.hash == hash &&
                   ((k = p.key) == key || (key != null && key.equals(k))))
                   e = p;
               else if (p instanceof TreeNode)
                   e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
               else {
                   for (int binCount = 0; ; ++binCount) {
                       if ((e = p.next) == null) {
                           p.next = newNode(hash, key, value, null);
                           if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                               treeifyBin(tab, hash);
                           break;
                       }
                       if (e.hash == hash &&
                           ((k = e.key) == key || (key != null && key.equals(k))))
                           break;
                       p = e;
                   }
               }
               if (e != null) { // existing mapping for key
                   V oldValue = e.value;
                   if (!onlyIfAbsent || oldValue == null)
                       e.value = value;
                   afterNodeAccess(e);
                   return oldValue;
               }
           }
           ++modCount;
           if (++size > threshold)
               resize();
           afterNodeInsertion(evict);
           return null;
       }
   ```

   

2. 关于HashMap中的节点Node

   ```java
   static class Node<K,V> implements Map.Entry<K,V> {
       	//通过对key进行一系列运算得出的hash值
           final int hash;
       	//key的值  类型通过泛型确定
           final K key;
       	//value的值  类型通过泛型确定
           V value;
       	//下一个节点的信息
           Node<K,V> next;
   
           Node(int hash, K key, V value, Node<K,V> next) {
               this.hash = hash;
               this.key = key;
               this.value = value;
               this.next = next;
           }
   
           public final K getKey()        { return key; }
           public final V getValue()      { return value; }
           public final String toString() { return key + "=" + value; }
   		//重写hashcode方法
           public final int hashCode() {
               return Objects.hashCode(key) ^ Objects.hashCode(value);
           }
   
           public final V setValue(V newValue) {
               V oldValue = value;
               value = newValue;
               return oldValue;
           }
   		//重写equals方法
           public final boolean equals(Object o) {
               if (o == this)
                   return true;
               if (o instanceof Map.Entry) {
                   Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                   if (Objects.equals(key, e.getKey()) &&
                       Objects.equals(value, e.getValue()))
                       return true;
               }
               return false;
           }
       }
   ```

3. 关于HashMap中的几个基准参数

   ```java
   //数组默认大小16 之所以用位运算符是因为位运算比直接写16效率更高  且必须是2的幂
   static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
   ```

   ```java
   	/**
        * The maximum capacity, used if a higher value is implicitly specified
        * by either of the constructors with arguments.
        * MUST be a power of two <= 1<<30.
        * 数组的最大容量为 2的30次幂  
        */
   static final int MAXIMUM_CAPACITY = 1 << 30;
   ```

   ```java
   	/**
        * The load factor used when none specified in constructor.
        * 默认的加载因子
        */
   	
       static final float DEFAULT_LOAD_FACTOR = 0.75f;
   ```

   ```java
   	/**
        * The bin count threshold for using a tree rather than list for a
        * bin.  Bins are converted to trees when adding an element to a
        * bin with at least this many nodes. The value must be greater
        * than 2 and should be at least 8 to mesh with assumptions in
        * tree removal about conversion back to plain bins upon
        * shrinkage.
        * 在箱子中的链表长度小于这个值时，箱子中的node以链表的形式进行存储，当箱子中的node数量大于这个		值时，箱子中node的存储方式转化为红黑树
        */
       static final int TREEIFY_THRESHOLD = 8;
   ```

   ```java
   	/**
        * The bin count threshold for untreeifying a (split) bin during a
        * resize operation. Should be less than TREEIFY_THRESHOLD, and at
        * most 6 to mesh with shrinkage detection under removal.
        * 在哈希表进行重建时，如果发现长度小于这个值  则会将由红黑树转换为链表
        */
       static final int UNTREEIFY_THRESHOLD = 6;
   ```

   ```java
   	/**
        * The smallest table capacity for which bins may be treeified.
        * (Otherwise the table is resized if too many nodes in a bin.)
        * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
        * between resizing and treeification thresholds.
        * 在转变成树之前，还会有一次判断，只有键值对数量大于 64 才会发生转换。这是为了避免在哈希表建立初期，多个键值对恰好被放入了同一个链表中而导致不必要的转化。
        */
       static final int MIN_TREEIFY_CAPACITY = 64;
   ```

   

4. 关于HashMap中的node节点的存储位置（个人认为这是整个源码中最精髓的部分）

   ```java
   
   
   final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                      boolean evict) {
           Node<K,V>[] tab; 
       	Node<K,V> p; 
       	int n, i;
       	//这一步在做判断的同时  将node数组的长度赋给了n
           if ((tab = table) == null || (n = tab.length) == 0)
               n = (tab = resize()).length;
       	//这里在做判断的同时，给i赋值为(n - 1) & hash ，同时把下标为i的位置的node赋值给了p（这意味着如果桶里不是空的，就会将桶最底部的一个node赋值给p）
           if ((p = tab[i = (n - 1) & hash]) == null)
               //如果i下标的位置为空，则直接将新传入的key/value等值存入这个下标为i的位置
               //之后就是对容量做一些判断，确定是否需要扩容
               tab[i] = newNode(hash, key, value, null);
           else {
               //如果下标为i的位置不为空 
               //新建一个Node对象e
               Node<K,V> e; 
               //新建一个K对象k
               K k;
               //这里的逻辑是：当传入的key与value值中，经过hash计算的key值与原本存在node节点中的某个key值相等（hash与equals都相等才算相等），
               //p.hash == hash 确定当前桶里面node的hash值与传入的hash值相等
               //(k = p.key) == key  将桶最底部node的key赋值给k 同时与传入的key做比较是否相等
               //如果满足以上两个条件，则将桶最底部的node赋值给e（为什么赋值来赋值去的？）
               if (p.hash == hash &&
                   ((k = p.key) == key || (key != null && key.equals(k))))
                   e = p;
               else if (p instanceof TreeNode)
                   //如果不相等，且p是一个树节点，会通过putTreeVal方法将key/value的值存储为这个红黑树的叶子节点，同时赋值给e
                   e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
               else {
                   //如果既不相等，也不是红黑树，则按照链表的方式进行添加
                   for (int binCount = 0; ; ++binCount) {
                       if ((e = p.next) == null) {
                           p.next = newNode(hash, key, value, null);
                           if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                               treeifyBin(tab, hash);
                           break;
                       }
                       if (e.hash == hash &&
                           ((k = e.key) == key || (key != null && key.equals(k))))
                           break;
                       p = e;
                   }
               }
               //这里是对key进行重新赋值
               if (e != null) { // existing mapping for key
                   V oldValue = e.value;
                   if (!onlyIfAbsent || oldValue == null)
                       e.value = value;
                   afterNodeAccess(e);
                   return oldValue;
               }
           }
           ++modCount;
           if (++size > threshold)
               resize();
           afterNodeInsertion(evict);
           return null;
       }
   ```

5. 获取方法

   ```java
       public V get(Object key) {
           Node<K,V> e;
           return (e = getNode(hash(key), key)) == null ? null : e.value;
       }
   
       /**
        * Implements Map.get and related methods
        *
        * @param hash hash for key
        * @param key the key
        * @return the node, or null if none
        */
       final Node<K,V> getNode(int hash, Object key) {
           Node<K,V>[] tab;
           Node<K,V> first, e; 
           int n; 
           K k;
           if ((tab = table) != null 
               && (n = tab.length) > 0 
               && (first = tab[(n - 1) & hash]) != null) {
               if (first.hash == hash 
                   && // always check first node
                   ((k = first.key) == key 
                   || (key != null && key.equals(k))
                   ))
                   return first;
               if ((e = first.next) != null) {
                   if (first instanceof TreeNode)
                       return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                   do {
                       if (e.hash == hash &&
                           ((k = e.key) == key || (key != null && key.equals(k))))
                           return e;
                   } while ((e = e.next) != null);
               }
           }
           return null;
       }
   ```

   

