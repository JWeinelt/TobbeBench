package de.julianweinelt.tobbql.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Configuration {
    private List<Project> projects = new ArrayList<>();
}
