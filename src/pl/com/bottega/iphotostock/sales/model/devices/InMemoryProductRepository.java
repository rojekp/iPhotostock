package pl.com.bottega.iphotostock.sales.model.devices;


import pl.com.bottega.iphotostock.sales.model.Client;
import pl.com.bottega.iphotostock.sales.model.Clip;
import pl.com.bottega.iphotostock.sales.model.Picture;
import pl.com.bottega.iphotostock.sales.model.Product;
import pl.com.bottega.iphotostock.sales.money.Money;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {

    private static final Map<String, Product> REPOSITORY = new HashMap<>();

    //blok static służy do zaimplementowania pól statycznych
    static {
        Collection<String> tags = Arrays.asList("przyroda,", "motoryzacja");
        Product product1 = new Picture("1", "BMW", tags, Money.valueOf(3));
        Product product2 = new Picture("2", "Mercedes", tags, Money.valueOf(2));
        Product product3 = new Picture("3", "Porsche", tags, Money.valueOf(4));
        Product clip1 = new Clip("Wsciekle piesci", Money.valueOf(10), "4", 2l * 1000 * 60 * 2);
        Product clip2 = new Clip("Sum tzw. olimpijczyk", Money.valueOf(11), "5", 4l * 1000 * 60 * 2);
        REPOSITORY.put("1", product1);
        REPOSITORY.put("2", product2);
        REPOSITORY.put("3", product3);
        REPOSITORY.put("4", clip1);
        REPOSITORY.put("5", clip2);
    }

    @Override
    public void put(Product product) {
        REPOSITORY.putIfAbsent(product.getNumber(), product);
    }

    @Override
    public Product get(String number) {
        return REPOSITORY.get(number);
    }

    @Override
    public List<Product> find(Client client, String nameQuery, String[] tags, Money priceFrom, Money priceTo, boolean onlyActive) {
        List<Product> matchingProducts = new LinkedList<>();
        for (Product product : REPOSITORY.values()) {
            if (matches(client, product, nameQuery, tags, priceFrom, priceTo, onlyActive)) {
                matchingProducts.add(product);
            }
        }
        return matchingProducts;
    }

    private boolean matches(Client client, Product product, String nameQuery, String[] tags, Money priceFrom, Money priceTo, boolean onlyActive) {

        return matchesQuery(product, nameQuery) &&
                matchesTags(product, tags) &&
                matchesPriceFrom(client, product, priceFrom) &&
                matchesPriceTo(client, product, priceTo);
    }

    private boolean matchesPriceTo(Client client, Product product, Money priceTo) {
        return priceTo == null || product.calculatePrice(client).lte(priceTo);
    }

    private boolean matchesPriceFrom(Client client, Product product, Money priceFrom) {
        return priceFrom == null ||product.calculatePrice(client).gte(priceFrom);
    }

    private boolean matchesTags(Product product, String[] tags) {
        if (tags == null || tags.length == 0)
            return true;

        if (!(product instanceof Picture))
            return false;

        Picture picture = (Picture) product;
        for (String tag : tags)
            if (!(picture.hasTag(tag)))
                return false;

        return true;
    }

    private boolean matchesQuery(Product product, String nameQuery) {
        return nameQuery == null || product.getName().toLowerCase().startsWith(nameQuery.toLowerCase());
    }


}
