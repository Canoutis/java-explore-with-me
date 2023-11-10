package ewm.event;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;

    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW,
        REJECT_EVENT,
        PUBLISH_EVENT,

    }

    public enum Sort {
        VIEWS,
        EVENT_DATE
    }
}
