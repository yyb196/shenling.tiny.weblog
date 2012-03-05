package shenling.weblog.beans;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-2 下午6:56
 */
@PersistenceCapable(detachable = "true")
public class SiteMetaInfo implements Serializable {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key = null;
    @Persistent
    private String title = "码农鱼塘";
    @Persistent
    private String host = "http://blog.yutown.com";
    @Persistent
    private String seed = "/blogFeed.do";
    @Persistent
    private String author = "码农";
    @Persistent
    private String keyWords = "java, cloud caculate, geek";
    @Persistent
    private int perPageCount = 10;
    @Persistent
    private String description = "记录自己的思考和成长";
    @Persistent
    private String subTitle = "小程序员的网络日志";
    @Persistent
    private String noticeBoard = "哥的博客开张了！";
    @Persistent
    private Text ads = new Text("");
    @Persistent
    private String year = "2012";
    @Persistent
    private String version = "1.0";

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getNoticeBoard() {
        return noticeBoard;
    }

    public void setNoticeBoard(String noticeBoard) {
        this.noticeBoard = noticeBoard;
    }

    public String getAdsStr() {
        return ads == null ? null : ads.getValue();
    }

    public void setAdsStr(String ads) {
        this.ads = new Text(ads);
    }

    public Text getAds() {
        return ads;
    }

    public void setAds(Text ads) {
        this.ads = ads;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public int getPerPageCount() {
        return perPageCount;
    }

    public void setPerPageCount(int perPageCount) {
        this.perPageCount = perPageCount;
    }
}
