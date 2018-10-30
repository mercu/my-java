package spring.jpa;

import com.mercu.bricklink.model.category.PartCategory;
import com.mercu.bricklink.repository.PartCategoryRepository;
import com.mercu.config.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class PartCategoryRepositoryTest {
    @Autowired
    private PartCategoryRepository partCategoryRepository;

    @Test
    public void test() {
//        PartCategory partCategory = new PartCategory();
//        partCategory.setId("1");
//        partCategoryRepository.save(partCategory);

        List<PartCategory> partCategoryList = (List<PartCategory>)partCategoryRepository.findAll();
        System.out.println("findAll : " + partCategoryList);
    }



}
