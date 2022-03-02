package me.rgunny.study.optional;

import java.util.Optional;

public class OnlineClass {

    private Integer id;

    private String title;

    private boolean closed;

    public Progress progress;

    public Optional<Progress> optionalProgress;

    public OnlineClass(Integer id, String title, boolean closed) {
        this.id = id;
        this.title = title;
        this.closed = closed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Progress getProgress() {
        return progress;
    }

    public Optional<Progress> getOptionalProgress() {
        return optionalProgress;
    }

    public Optional<Progress> getProgressByOptional() {
        return Optional.ofNullable(progress);
    }

    public Optional<Progress> getProgressByOptionalReturnEmpty() {
//        return null; // do not use ever
        return Optional.empty();
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    /**
     * Optional을 파라미터로 사용하지 말자
     * => 오히려 별도의 체크를 더 해야 함
     */
    public void setProgressByOptionalParameter(Optional<Progress> progress) {
        progress.ifPresent(p -> this.progress = p);
//        if (progress != null) {
//            progress.ifPresent(p -> this.progress = p);
//        }
    }
}
