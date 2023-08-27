
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HitListDto {
    private List<HitGetDto> hits;

    public HitListDto() {
        hits = new ArrayList<>();
    }
}
