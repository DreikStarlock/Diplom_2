package pojo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class IngredientsResponsePojo {
    private boolean success;
    private List<IngredientPojo> data;
}
