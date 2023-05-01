package me.rgunny.itemservice.web.basic;

import lombok.RequiredArgsConstructor;
import me.rgunny.itemservice.domain.Item;
import me.rgunny.itemservice.domain.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

//    @Autowired
//    public BasicItemController(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                            @RequestParam int price,
                            @RequestParam Integer quantity,
                            Model model) {

        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);

        return "basic/item";
    }

    /**
     * @ModelAttribute 기능
     * 1. @ModelAttribute 는 Item 객체 생성 후 요청 파라미터의 값을 프로퍼티 접근법(setXxx)로 입력해줌
     * 2. Model 에 @ModelAttribute 로 지정한 객체를 자동으로 넣어줌. -> model.addAttribute("객체 이름", 객체이름) --> 객체이름은 name(value) 속성을 따라감
     */
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item, Model model) {

        itemRepository.save(item);
//      model.addAttribute("item", item); // @ModelAttribute 사용 시, 자동 추가 (동일 이름) --> 생략 가능

        return "basic/item";
    }

    /**
     * @ModelAttribute 기능
     * 3. name 속성을 넣지 않을 때, 클래스일 경우 lowerCamelCase 를 적용한 이름을 따름
     *   : ex) HelloData -> helloData
     */
//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * @ModelAttribute 생략
     *   : String 등 단순 타입이 오면 @RequestParam 적용, 임의의 객체가 오면 @ModelAttribute 적용
     *   --> 생략 시, 3. 기능을 따라 lowerCamelCase 를 적용한 이름을 적용하게 됨
     */
//    @PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}

