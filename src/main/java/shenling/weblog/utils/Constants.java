package shenling.weblog.utils;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-8 下午6:56
 */
public class Constants {
    private static volatile long index = 30L;
    //固定的key
    public static volatile String SiteMetaInfoKey = "SiteMetaInfoKey$" + index;
    public static volatile String LinkList = "LinkList$" + index;
    //key prefix
    public static volatile String ArticleListCachePrefix = "ArticleListCachePrefix$" + index;
    public static volatile String ArticleCachePrefix = "ArticleCachePrefix$" + index;
    public static volatile String CommnetListCachePrefix = "CommentListCachePrefix$" + index;
    public static volatile String CommnetRecentCachePrefix = "CommentRecentCachePrefix$" + index;
    //
    public static volatile String ArticlePreviousPrefix = "ArticlePreviousPrefix$" + index;
    public static volatile String ArticleNextPrefix = "ArticleNextPrefix" + index;

    public static volatile String BFilePrefix = "BFile$" + index;
    //两个总开关
    public static volatile String TotalCommentPrefix = "Comment";
    public static volatile String TotalArticlePrefix = "Article";

    //代替缓存入库的，不需要释放和改变
    public static volatile String SiteData = "SiteData";
    public static volatile String DataArticlePrefix = "DataAritcle$";


    public static void free(String key) {
        if (SiteMetaInfoKey.equals(key)) {
            SiteMetaInfoKey += ++index;
        } else if (ArticleListCachePrefix.equals(key)) {
            ArticleListCachePrefix += ++index;
        } else if (ArticleCachePrefix.equals(key)) {
            ArticleCachePrefix += ++index;
        } else if (CommnetListCachePrefix.equals(key)) {
            CommnetListCachePrefix += ++index;
        } else if (CommnetRecentCachePrefix.equals(key)) {
            CommnetRecentCachePrefix += ++index;
        } else if (LinkList.equals(key)) {
            LinkList += ++index;
        } else if (SiteData.equals(key)) {
//            SiteData += ++index;
        } else if (DataArticlePrefix.equals(key)) {
//            DataArticlePrefix += ++index;
        } else if (TotalCommentPrefix.equals(key)) {
            CommnetListCachePrefix += ++index;
            CommnetRecentCachePrefix += ++index;
        } else if (TotalArticlePrefix.equals(key)) {
            ArticleListCachePrefix += ++index;
            ArticleCachePrefix += ++index;
            ArticlePreviousPrefix += ++index;
            ArticleNextPrefix += ++index;
        } else if (BFilePrefix.equals(key)) {
            BFilePrefix += ++index;
        } else {
            freeWithPrefix(key);
        }
    }

    public static void freeWithPrefix(String prefix) {
        if (SiteMetaInfoKey.startsWith(prefix)) {
            SiteMetaInfoKey += ++index;
        } else if (TotalCommentPrefix.equals(prefix)) {
            CommnetListCachePrefix += ++index;
            CommnetRecentCachePrefix += ++index;
        } else if (TotalArticlePrefix.equals(prefix)) {
            ArticleListCachePrefix += ++index;
            ArticleCachePrefix += ++index;
            ArticlePreviousPrefix += ++index;
            ArticleNextPrefix += ++index;
        } else if (ArticleListCachePrefix.startsWith(prefix)) {
            ArticleListCachePrefix += ++index;
        } else if (ArticleCachePrefix.startsWith(prefix)) {
            ArticleCachePrefix += ++index;
        } else if (CommnetListCachePrefix.startsWith(prefix)) {
            CommnetListCachePrefix += ++index;
        } else if (CommnetRecentCachePrefix.startsWith(prefix)) {
            CommnetRecentCachePrefix += ++index;
        } else if (LinkList.startsWith(prefix)) {
            LinkList += ++index;
        } else if (SiteData.startsWith(prefix)) {
//            SiteData += ++index;
        } else if (DataArticlePrefix.startsWith(prefix)) {
//            DataArticlePrefix += ++index;
        } else if (BFilePrefix.startsWith(prefix)) {
            BFilePrefix += ++index;
        }
    }

    public static void freeAll() {
        SiteMetaInfoKey += ++index;
        ArticleListCachePrefix += ++index;
        ArticleCachePrefix += ++index;
        CommnetListCachePrefix += ++index;
        CommnetRecentCachePrefix += ++index;
        LinkList += ++index;
        SiteData += ++index;
        DataArticlePrefix += ++index;
        TotalCommentPrefix += ++index;
        TotalArticlePrefix += ++index;
        BFilePrefix += ++index;
    }
}
