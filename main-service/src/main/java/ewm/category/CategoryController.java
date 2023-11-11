package ewm.category;

import ewm.category.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = "/admin/categories")
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.create(categoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/admin/categories/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable int id) {
        categoryService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/admin/categories/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable int id, @Valid @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.updateById(id, categoryDto), HttpStatus.OK);
    }

    @GetMapping(value = "/categories")
    public ResponseEntity<List<CategoryDto>> findCategories(@RequestParam(defaultValue = "0", required = false) Integer from,
                                                            @RequestParam(defaultValue = "10", required = false) Integer size) {
        return new ResponseEntity<>(categoryService.findCategories(from, size), HttpStatus.OK);
    }

    @GetMapping(value = "/categories/{catId}")
    public ResponseEntity<CategoryDto> findCategories(@PathVariable Integer catId) {
        return new ResponseEntity<>(categoryService.findCategory(catId), HttpStatus.OK);
    }
}
