package com.primedsoft.primedclass.Model;

public class RequestedTeacherModel {

    private String  parentsName,parentsUid,subjectRequested,classRequested,childsPushKey;

    public RequestedTeacherModel() {
    }

    public String getClassRequested() {
        return classRequested;
    }

    public void setClassRequested(String classRequested) {
        this.classRequested = classRequested;
    }

    public String getParentsName() {
        return parentsName;
    }

    public void setParentsName(String parentsName) {
        this.parentsName = parentsName;
    }

    public String getParentsUid() {
        return parentsUid;
    }

    public void setParentsUid(String parentsUid) {
        this.parentsUid = parentsUid;
    }

    public String getSubjectRequested() {
        return subjectRequested;
    }

    public void setSubjectRequested(String subjectRequested) {
        this.subjectRequested = subjectRequested;
    }

    public String getChildsPushKey() {
        return childsPushKey;
    }

    public void setChildsPushKey(String childsPushKey) {
        this.childsPushKey = childsPushKey;
    }
}
