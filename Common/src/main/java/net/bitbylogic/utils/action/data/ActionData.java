package net.bitbylogic.utils.action.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class ActionData<T> {

    private final @NotNull T data;

}
