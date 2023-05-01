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
     * 새로고침 : 마지막에 했던 요청을 다시 요청
     *  --> POST 가 계속 저장되는 문제
     *  --> ID만 다른 상품등록이 계속 등록됨
     *
     * PRG : Post/Redirect/Get
     *   새로고침 문제 해결 : 상품 저장(POST /add) 후 뷰 템플릿으로 이동하는 것이 아닌, 상품 상세 화면으로 리다이렉트 호출 (Redirect /items/{id}
     *   웹 브라우저는 리다이렉트로 인해 상품 저장 후 상품 상세 화면으로 이동
     *   즉, 마지막 호출 내용은 상품 상세 화면인, GET /items/{id}
     *
     */
//    @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId();
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    /**
     * 스프링은 redirect:/... 으로 편리하게 리다이렉트 지원
     * 컨트롤러에 매핑된 @PathVariable 값은 redirect 에도 사용 가능
     *
     * HTML Form 전송은 PUT, PATCH 를 지원하지 않고, GET, POST 만 지원
     */
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
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

