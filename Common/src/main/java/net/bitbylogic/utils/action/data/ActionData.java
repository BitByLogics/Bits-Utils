package net.bitbylogic.utils.action.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ActionData<T> {

    private final T data;

}
