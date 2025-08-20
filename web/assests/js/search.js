/* global Swal */


async function loadData() {

    const response = await fetch("LoadData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            loadOptions("brand", json.brandList, "name");
            loadOptions("condition", json.qualityList, "name");
            loadOptions("category", json.categoryList, "name");
            loadOptions("ram", json.ramList, "name");

            loadOptions("color", json.colorList, "name");
            loadOptions("storage", json.storageList, "name");
            loadOptions("processor", json.processorList, "name");
            loadOptions("graphic", json.graphicList, "name");

            updateProductView(json);
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: "Somthing went wrong"

            });


        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Somthing went wrong"

        });

    }
}

function handleCategoryChange(selectedCategory) {
    const laptopFilters = document.getElementById("laptop-filters");

    // Convert category to lowercase for safe comparison
    const category = selectedCategory.toLowerCase();

    if (category === "laptop" || category === "desktop") {
        laptopFilters.style.display = "block";
    } else {
        laptopFilters.style.display = "none";
    }
}


function loadOptions(prefix, dataList, property) {
    const container = document.getElementById(prefix + "-options");
    container.innerHTML = ""; // Clear old options

    dataList.forEach((item, index) => {
        const id = `${prefix}-${index}`;
        const value = item[property];

        const optionDiv = document.createElement("div");
        optionDiv.className = "filter-option";

        const input = document.createElement("input");
        input.type = "radio";
        input.id = id;
        input.name = prefix;
        input.value = value;


        if (prefix === "category") {
            input.addEventListener("change", function () {
                handleCategoryChange(this.value);
            });
        }

        const label = document.createElement("label");
        label.htmlFor = id;
        label.innerHTML = value;

        optionDiv.appendChild(input);
        optionDiv.appendChild(label);

        container.appendChild(optionDiv);
    });
}


async function searchProduct(firstResult) {

    const category_name = document.querySelector('input[name="category"]:checked')?.value;
    const brand_name = document.querySelector('input[name="brand"]:checked')?.value;
    const condition_name = document.querySelector('input[name="condition"]:checked')?.value;
    const color_name = document.querySelector('input[name="color"]:checked')?.value;
    const storage_value = document.querySelector('input[name="storage"]:checked')?.value;
    const ram_value = document.querySelector('input[name="ram"]:checked')?.value;
    const processor_value = document.querySelector('input[name="processor"]:checked')?.value;
    const graphic_value = document.querySelector('input[name="graphic"]:checked')?.value;

    const price_range_start = document.getElementById("min-slider").value;
    const price_range_end = document.getElementById("max-slider").value;
    const sort_value = document.getElementById("st-sort").value;

    const data = {
        firstResult: firstResult,
        categoryName: category_name,
        brandName: brand_name,
        conditionName: condition_name,
        colorName: color_name,
        storageValue: storage_value,
        ramValue: ram_value,
        processorValue: processor_value,
        graphicValue: graphic_value,
        priceStart: parseFloat(price_range_start),
        priceEnd: parseFloat(price_range_end),
        sortValue: sort_value
    };

    const dataJSON = JSON.stringify(data);

    //console.log(dataJSON);
    const response = await fetch("SearchProducts",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: dataJSON
            });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            updateProductView(json);
            Swal.fire({
                icon: 'success',
                title: 'Search Success',
                text: "Product Loading Complete..."

            });

        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: "Somthing went wrong. Please try again later"

            });


        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Somthing went wrong. Please try again later"

        });
    }
}

const st_product = document.getElementById("st-product"); // product card parent node
let st_pagination_button = document.getElementById("st-pagination-button");
let current_page = 0;

function updateProductView(json) {
    const product_container = document.getElementById("st-product-container");
    product_container.innerHTML = "";
    json.productList.forEach(product => {
        let st_product_clone = st_product.cloneNode(true);// enable child nodes cloning / allow child nodes
        st_product_clone.querySelector(".st-product-link").href = "single-product.html?id=" + product.id;
        st_product_clone.querySelector(".st-product-img").src = "product-images/" + product.id + "/image1.png";
        st_product_clone.querySelector(".st-product-title").innerHTML = product.title;
        st_product_clone.querySelector(".st-product-price").innerHTML = new Intl.NumberFormat("en-US", {
            minimumFractionDigits: 2
        }).format(product.price);
        st_product_clone.querySelector(".st-product-add-to-cart").addEventListener("click", (e) => {
            addToCart(product.id, 1);
            e.preventDefault();
        });

        //append child
        product_container.appendChild(st_product_clone);
    });

    let st_pagination_container = document.getElementById("st-pagination-container");
    st_pagination_container.innerHTML = "";

    let all_product_count = json.allProductCount;
    document.getElementById("all-item-count").innerHTML = all_product_count;

    let product_per_page = 5;
    let pages = Math.ceil(all_product_count / product_per_page);

    function createButton(text, onClick, isActive = false) {
        let btn = document.createElement("a");
        btn.href = "#";
        btn.textContent = text;
        btn.className = "page-btn" + (isActive ? " active" : "");
        btn.addEventListener("click", (e) => {
            e.preventDefault();
            onClick();
        });
        return btn;
    }

// Prev button
    if (current_page > 0) {
        st_pagination_container.appendChild(
                createButton("Prev", () => {
                    current_page--;
                    searchProduct(current_page * product_per_page);
                })
                );
    }

// Page numbers
    for (let i = 0; i < pages; i++) {
        st_pagination_container.appendChild(
                createButton(i + 1, () => {
                    current_page = i;
                    searchProduct(i * product_per_page);
                }, i === current_page)
                );
    }

// Next button
    if (current_page < pages - 1) {
        st_pagination_container.appendChild(
                createButton("Next", () => {
                    current_page++;
                    searchProduct(current_page * product_per_page);
                })
                );
    }


}

async function addToCart(productId, qty) {

    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            Swal.fire({
                icon: 'success',
                title: 'Search Success',
                text: json.message

            });

        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: "Somthing went wrong. Please try again later"

            });

        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Somthing went wrong. Please try again later"

        });
    }
}