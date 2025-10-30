package net.bitbylogic.utils.abstraction;

import lombok.NonNull;

public interface Viewable<T> {

    boolean canView(@NonNull T viewer);

}
