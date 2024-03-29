package com.chen.website.utils;

import java.util.*;

/**
 * 评论过滤工具类
 * <p>提供了对外的{@link #filter(String)}方法</p>
 * <pre>
 *     {@link #filter(String)}方法，将字符串传入，
 *     通过工具类中封装的静态方法处理字符串，返回脱敏后的字符串。
 * </pre>
 * @author ChenetChen
 * @since 2021/4/29 13:52
 */
public class BadWordUtils {

    public static Set<String> words;
    public static List<String> wordText = new ArrayList<>();
    public static List<String> word= new ArrayList<>();
    public static Map<String,String> wordMap;
    public static Map<String,String> wordMaps;
    public static int minMatchTYpe = 1;      //最小匹配规则
    public static int maxMatchType = 2;      //最大匹配规则

    static{
        BadWordUtils.words = readTxtByLine(wordText,word);
        addBadWordToHashMap(BadWordUtils.words);

    }

    public static Set<String> readTxtByLine(List<String> wordText,List<String> word){
        wordText.addAll(word);
        Set<String> keyWordSet = new HashSet<String>();
        try{
            keyWordSet.addAll(wordText);
        } catch(Exception e){
            e.printStackTrace();
        }
        return keyWordSet;
    }

    /**
     * 检查文字中是否包含敏感字符，检查规则如下：<br>
     * @param txt
     * @param beginIndex
     * @param matchType
     * @return，如果存在，则返回敏感词字符的长度，不存在返回0
     */
    @SuppressWarnings({ "rawtypes"})
    public static int checkBadWord(String txt,int beginIndex,int matchType){
        boolean  flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况
        int matchFlag = 0;     //匹配标识数默认为0
        char word = 0;
        Map nowMap = wordMap;

        for(int i = beginIndex; i < txt.length() ; i++){
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);     //获取指定key
            if(nowMap != null){     //存在，则判断是否为最后一个
                matchFlag++;     //找到相应key，匹配标识+1
                if("1".equals(nowMap.get("isEnd"))){       //如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag = true;       //结束标志位为true
                    if(minMatchTYpe == matchType){    //最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            }
            else{     //不存在，直接返回
                break;
            }
        }
        if(!flag){
            matchFlag = 0;
        }
        return matchFlag;
    }

    /**
     * 判断文字是否包含敏感字符
     * @param txt  文字
     * @param matchType  匹配规则 1：最小匹配规则，2：最大匹配规则
     * @return 若包含返回true，否则返回false
     */
    public static boolean isContaintBadWord(String txt,int matchType){
        boolean flag = false;
        for(int i = 0 ; i < txt.length() ; i++){
            int matchFlag = checkBadWord(txt, i, matchType); //判断是否包含敏感字符
            if(matchFlag > 0){    //大于0存在，返回true
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 替换敏感字字符
     * @param txt
     * @param matchType
     * @param replaceChar 替换字符，默认*
     */
    public static String replaceBadWord(String txt,int matchType,String replaceChar){
        String resultTxt = txt;
        Set<String> set = getBadWord(txt, matchType);     //获取所有的敏感词
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());

            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 获取文字中的敏感词
     * @param txt 文字
     * @param matchType 匹配规则 1：最小匹配规则，2：最大匹配规则
     * @return
     */
    public static Set<String> getBadWord(String txt , int matchType){
        Set<String> sensitiveWordList = new HashSet<String>();

        for(int i = 0 ; i < txt.length() ; i++){
            int length = checkBadWord(txt, i, matchType);    //判断是否包含敏感字符
            if(length > 0){    //存在,加入list中
                sensitiveWordList.add(txt.substring(i, i+length));
                i = i + length - 1;    //减1的原因，是因为for会自增
            }
        }
        return sensitiveWordList;
    }

    /**
     * 获取替换字符串
     * @param replaceChar
     * @param length
     * @return
     * @version 1.0
     */
    private static String getReplaceChars(String replaceChar,int length){
        String resultReplace = replaceChar;
        for(int i = 1 ; i < length ; i++){
            resultReplace += replaceChar;
        }
        return resultReplace;
    }

    /**
     * 将敏感词库构建成了一个类似与一颗一颗的树，这样我们判断一个词是否为敏感词时就大大减少了检索的匹配范围。
     * @param keyWordSet 敏感词库
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void addBadWordToHashMap(Set<String> keyWordSet) {
        wordMap = new HashMap(keyWordSet.size());     //初始化敏感词容器，减少扩容操作
        String key = null;
        Map nowMap = null;
        Map<String, String> newWorMap = null;
        //迭代keyWordSet
        Iterator<String> iterator = keyWordSet.iterator();
        while(iterator.hasNext()){
            key = iterator.next();    //关键字
            nowMap = wordMap;
            for(int i = 0 ; i < key.length() ; i++){
                char keyChar = key.charAt(i);       //转换成char型
                Object wordMap = nowMap.get(keyChar);       //获取

                if(wordMap != null){        //如果存在该key，直接赋值
                    nowMap = (Map) wordMap;
                }
                else{     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<String,String>();
                    newWorMap.put("isEnd", "0");     //不是最后一个
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if(i == key.length() - 1){
                    nowMap.put("isEnd", "1");    //最后一个
                }
            }
        }
    }

    /**
     * 对外接口
     * @param string 传入的字符串
     * @return 返回脱敏后的字符串
     */
    public static String filter(String string){
        List<String> wordText = new ArrayList<>();
        //可以使用配置文件来加载敏感词
        List<String> word= Arrays.asList(
                "中央","中共","组织","部门",
                "妈的","TMD","si","死","艹"
        );

        Set<String> strings = BadWordUtils.readTxtByLine(wordText, word);
        BadWordUtils.addBadWordToHashMap(strings);
        BadWordUtils.getBadWord(string,2);
        return BadWordUtils.replaceBadWord(string, 2, "*");
    }

    public static void main(String[] args) {
        List<String> wordText = new ArrayList<>();
        List<String> word= new ArrayList<>();
        word.add("组织");
        word.add("必须");
        word.add("部门");
        //在实际需要调用需要脱敏处理的业务处需要改动，下面有例子。word相当于你配置的敏感词
        Set<String> strings = BadWordUtils.readTxtByLine(wordText, word);
        BadWordUtils.addBadWordToHashMap(strings);
        System.out.println("敏感词的数量：" + BadWordUtils.wordMap.size());
        String string = "这里是必要组织，高级部门，必须";
        System.out.println("待检测语句字数：" + string.length());
        long beginTime = System.currentTimeMillis();
        Set<String> set = BadWordUtils.getBadWord(string, 2);
        long endTime = System.currentTimeMillis();
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        String s1 = BadWordUtils.replaceBadWord(string, 2, "*");
        System.out.println("脱敏后的语句为："+s1);
        System.out.println("总共消耗时间为：" + (endTime - beginTime));
        System.out.println("==============");
        System.out.println(BadWordUtils.filter("这里是必要到，高级部门，必须"));
    }
}