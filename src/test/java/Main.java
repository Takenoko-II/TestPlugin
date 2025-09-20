import com.gmail.subnokoii78.tplcore.execute.NumberRange;
import com.gmail.subnokoii78.tplcore.random.AbstractCountable;
import com.gmail.subnokoii78.tplcore.random.RandomService;
import com.gmail.subnokoii78.tplcore.random.Xorshift32;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        final RandomService randomService = new RandomService(new Xorshift32(1));

        final List<ItemStack> itemStacks = new ArrayList<>(List.of(
            new ItemStack("apple", 30),
            new ItemStack("banana", 20),
            new ItemStack("grape", 10)
        ));

        randomService.split(itemStacks, 50, 0.7f);
        System.out.println(randomService.shuffledClone(itemStacks));
    }
}
