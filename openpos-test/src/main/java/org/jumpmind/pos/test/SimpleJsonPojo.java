package org.jumpmind.pos.test;

import java.util.Date;

public class SimpleJsonPojo {

    private String id;
    private Date createDate;
    private long sequence;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public long getSequence() {
        return sequence;
    }
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (sequence ^ (sequence >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleJsonPojo other = (SimpleJsonPojo) obj;
        if (createDate == null) {
            if (other.createDate != null)
                return false;
        } else if (!createDate.equals(other.createDate))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (sequence != other.sequence)
            return false;
        return true;
    } 
    
    @Override
    public String toString() {
        return "SimpleJsonPojo [id=" + id + ", createDate=" + createDate + ", sequence=" + sequence + "]";
    }
    
}
