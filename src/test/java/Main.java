import com.gmail.subnokoii78.tplcore.random.AbstractCountable;
import org.jetbrains.annotations.NotNull;

public class Main {
    public static final class ItemStack extends AbstractCountable<String> {
        public ItemStack(@NotNull String data, int count) {
            super(data, count);
        }

        public @NotNull String getType() {
            return value;
        }

        @Override
        public String toString() {
            return String.format("{id: %s, count: %d}", getType(), getCount());
        }

        @Override
        public @NotNull AbstractCountable<String> copy() {
            return new ItemStack(value, count);
        }
    }

    public static void main(String[] args) {

    }
}
