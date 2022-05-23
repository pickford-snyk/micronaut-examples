/*
 * Copyright 2018 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.storefront;

import example.api.v1.Offer;
import example.api.v1.Pet;
import example.api.v1.Vendor;
import example.storefront.client.v1.CommentClient;
import example.storefront.client.v1.PetClient;
import example.storefront.client.v1.TweetClient;
import example.storefront.client.v1.VendorClient;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.sse.Event;
import io.micronaut.reactor.http.client.ReactorStreamingHttpClient;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

/**
 * @author graemerocher
 * @since 1.0
*/
@Controller("/")
public class StoreController {

    private final ReactorStreamingHttpClient offersClient;
    private final VendorClient vendorClient;
    private final PetClient petClient;
    private final CommentClient commentClient;
    private final TweetClient tweetClient;

    public StoreController(
            @Client(id = "offers") ReactorStreamingHttpClient offersClient,
            VendorClient vendorClient,
            PetClient petClient,
            CommentClient commentClient,
            TweetClient tweetClient) {
        this.offersClient = offersClient;
        this.vendorClient = vendorClient;
        this.petClient = petClient;
        this.commentClient = commentClient;
        this.tweetClient = tweetClient;
    }

    @Produces(MediaType.TEXT_HTML)
    @Get(uri = "/")
    public HttpResponse<?> index() {
        return HttpResponse.redirect(URI.create("/index.html"));
    }

    @Get(uri = "/offers", produces = MediaType.TEXT_EVENT_STREAM)
    @SingleResult
    public Publisher<Event<Offer>> offers() {
        return Mono.from(offersClient.jsonStream(HttpRequest.GET("/v1/offers"), Offer.class)).map(Event::of);
    }

    @Get("/pets")
    @SingleResult
    public Publisher<Pet> pets() {
        // FIXME: not sure what this should be now.
        // old: Single<List<Pet>> petClient.list().onErrorReturnItem(Collections.emptyList())
        return petClient.list();
    }

    @Get("/pets/{slug}")
    @SingleResult
    public Publisher<Pet> showPet(@PathVariable("slug") String slug) {
        return petClient.findBySlug(slug);
    }

    @Get("/pets/random")
    @SingleResult
    public Publisher<Pet> randomPet() {
        return petClient.random();
    }


    @Get("/pets/vendor/{vendor}")
    @SingleResult
    public Publisher<Pet> petsForVendor(String vendor) {
        // FIXME: not sure what this should be now.
        // old: Single<List<Pet>> petClient.byVendor(vendor).onErrorReturnItem(Collections.emptyList());
        return petClient.findByVendor(vendor);
    }

    @Get("/vendors")
    public Publisher<Vendor> vendors() {
        // FIXME: not sure what this should be now.
        // old: Single<List<Vendor>> vendorClient.list().onErrorReturnItem(Collections.emptyList());
        return vendorClient.list();
    }
}
