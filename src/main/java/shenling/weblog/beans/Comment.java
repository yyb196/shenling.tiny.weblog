package shenling.weblog.beans;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 *
 * @author shenling.yyb
 * @since 12-1-2 下午9:58
 */
@PersistenceCapable(detachable = "true")
public class Comment implements Serializable {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key = null;
    @Persistent
    private String articleLink;
    /**
     * 评论者的名字
     */
    @Persistent
    private String title;
    @Persistent
    private String thumbnailURL = "/images/thumbnail.png";
    @Persistent
    private String url = "/";
    @Persistent
    private Text content;
    /**
     * 评论者的个人网址
     */
    @Persistent
    private String commenterUrl = "/";
    @Persistent
    private String oId = String.valueOf(System.nanoTime());
    @Persistent
    private Date commentDate = new Date();
    @Persistent
    private String email;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getArticleLink() {
        return articleLink;
    }

    public void setArticleLink(String articleLink) {
        this.articleLink = articleLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Text getContent() {
        return content;
    }

    public String getStrContent() {
        if (content == null) {
            return null;
        }
        return content.getValue();
    }

    public void setContent(Text content) {
        this.content = content;
    }

    public String getoId() {
        return oId;
    }

    public void setoId(String oId) {
        this.oId = oId;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCommenterUrl() {
        return commenterUrl;
    }

    public void setCommenterUrl(String commenterUrl) {
        this.commenterUrl = commenterUrl;
    }
}
