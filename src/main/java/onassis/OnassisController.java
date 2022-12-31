package onassis;

import java.util.List;

public class OnassisController {

    public static class Updates<T> {
        List<T> created;

        List<Integer> deleted;

        List<T> modified;

        @SuppressWarnings("unused")
		public List<T> getCreated() {
            return created;
        }

        @SuppressWarnings("unused")
        public void setCreated(List<T> created) {
            this.created = created;
        }

        @SuppressWarnings("unused")
        public List<Integer> getDeleted() {
            return deleted;
        }

        @SuppressWarnings("unused")
        public void setDeleted(List<Integer> deleted) {
            this.deleted = deleted;
        }

        @SuppressWarnings("unused")
        public List<T> getModified() {
            return modified;
        }

        @SuppressWarnings("unused")
        public void setModified(List<T> modified) {
            this.modified = modified;
        }

        @Override
        public String toString() {
            return "Updates{" +
                    "created=" + created +
                    ", deleted=" + deleted +
                    ", modified=" + modified +
                    '}';
        }
    }
}
