package shenling.weblog.beans;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-3 ÏÂÎç2:51
 */
@PersistenceCapable(detachable = "true")
public class Draft {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key = null;
    @Persistent
    private String link;
    @Persistent
    private String title;
    @Persistent
    private Date createDate = new Date();
    @Persistent
    private long commentCount = 0;
    @Persistent
    private Text content;
    @Persistent
    private long viewCount = 0;
    @Persistent
    private String tags;
    @Persistent
    private Date updateDate = new Date();
    @Persistent
    private Text other = null;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public Text getContent() {
        return content;
    }

    public void setContent(Text content) {
        this.content = content;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Text getOther() {
        return other;
    }

    public void setOther(Text other) {
        this.other = other;
    }

    public String getStrContent() {
        if (content == null) {
            return null;
        }
        return content.getValue();
    }

    public String getStrKey() {
        Key key = getKey();
        if (key == null) {
            return null;
        }
        return KeyFactory.keyToString(key);
    }

    public void set(Article article) {
        /*this.setCommentCount(article.getCommentCount());
        this.setCreateDate(article.getCreateDate());
        this.setOther(article.getOther());
        this.setUpdateDate(article.getUpdateDate());
                this.setViewCount(article.getViewCount());*/
        this.setLink(article.getLink());
        this.setContent(article.getContent());
        this.setTags(article.getTags());
        this.setTitle(article.getTitle());
    }
}
