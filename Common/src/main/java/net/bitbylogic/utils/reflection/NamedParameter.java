package net.bitbylogic.utils.reflection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NamedParameter {

    private final String name;
    private final Class<?> valueClass;
    private final Object value;

}
