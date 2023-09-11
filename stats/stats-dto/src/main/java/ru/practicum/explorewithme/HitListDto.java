package ru.practicum.explorewithme;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HitListDto {
    private List<HitGetDto> hits = new ArrayList<>();
}
