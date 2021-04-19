package com.telcreat.aio.service;

import com.telcreat.aio.model.Item;
import com.telcreat.aio.model.Variant;
import com.telcreat.aio.repo.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final VariantService variantService;
    private final ItemRepo itemRepo;
    private int code = 23;

    @Autowired
    public ItemService(ItemRepo itemRepo, VariantService variantService){
        this.itemRepo = itemRepo;
        this.variantService = variantService;
    }

    // Basic method - Find Item By Id
    public Item findItemById(int itemId){

        Item item = null;
        Optional<Item> opt = itemRepo.findById(itemId);
        if(opt.isPresent()){
            item = opt.get();
            code = 0;
        }else{
            code = 1;
        }
        return item;
    }

    // Basic method - Create New Item
    // Returns New Item if found and Null if not found
    public Item createItem(Item newItem){
        Item tempItem = null;
        if (!itemRepo.existsById(newItem.getId())){
            tempItem = itemRepo.save(newItem);
        }
        return tempItem;
    }

    // Basic method - Update Item
    // Returns Updated Item if found and Null if not found
    public Item updateItem(Item item){
        Item tempItem = null;
        if (itemRepo.existsById(item.getId())){
            tempItem = itemRepo.save(item);
        }
        return tempItem;
    }

    // Basic method - Delete Item
    // Returns TRUE if deleted and FALSE if not
    public boolean deleteItemById(int itemId){
        boolean control = false;
        if (itemRepo.existsById(itemId)){
            itemRepo.deleteById(itemId);
            control = true;
        }
        return control;
    }

    // Find Items By CategoryId and filtered
    public List<Item> findItemsContainsNameOrdered(String itemName, int orderCriteriaId, int itemCategoryId){

        // orderCriteriaId = 0 -> PRECIO
        // orderCriteriaId = 1 -> DISTANCIA
        // itemCategoryId = 0 -> DUMMY VARIABLE SOLO EN FRONTEND. Primera categoría empieza por ID=1.
        List<Item> items = findItemsContainsName(itemName,itemCategoryId);

        if(orderCriteriaId==0){

            for(int i=0;i<(items.size()-1);i++){
                for(int j=i+1;j<items.size();j++){
                    if(items.get(i).getPrice()>items.get(j).getPrice()){
                        float aux=items.get(i).getPrice();
                        items.get(i).setPrice(items.get(j).getPrice());
                        items.get(j).setPrice(aux);
                    }
                }
            }
        }else{

            /// ORDENAR PRODUCTOS POR DISTANCIA

        }
        return items;
    }

    public List<Item> findItemsContainsName(String itemName, int itemCategoryId){

        return itemRepo.findItemsByItemCategory_IdAndNameIsContaining(itemCategoryId,itemName);

    }
        // Find Items By Shop
    public List<Item> findAllItemsByShop(int shopId){
        return itemRepo.findItemsByShop_Id(shopId);
    }

    public Item deactivateItem(Item item){
        Item itemTemp = null;
        if(itemRepo.existsById(item.getId()) && item.getStatus().toString().equals("ACTIVE")){
            itemTemp=item;

            List<Variant> variants = variantService.findVariantsByItemId(itemTemp.getId());
            for (Variant variant:variants) {
                variantService.deactivateVariant(variant);
            }

            itemTemp.setStatus(Item.Status.valueOf("INACTIVE"));
            itemRepo.save(itemTemp);
        }
        return itemTemp;
    }
}
