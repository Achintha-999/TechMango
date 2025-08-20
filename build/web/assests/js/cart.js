/* global Swal */

async function loadCartItems() {
    const response = await fetch("LoadCartItems");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const cart_item_container = document.getElementById("cart-item-container");
            cart_item_container.innerHTML = "";

            let total = 0;
            let totalQty = 0;
            json.cartItems.forEach(cart => {
                let productSubTotal = cart.product.price * cart.qty;
                total += productSubTotal;
                totalQty += cart.qty;
                let tableData = `  <tr id="cart-item-row">
                                            <td class="product-info">
                                                <div class="product-image">
                                                    <img src="product-images\\${cart.product.id}\\image1.png" alt="Product">
                                                </div>
                                                <div class="product-details">
                                                    <h3>${cart.product.title}</h3>                                        
                                                </div>
                                            </td>
                                            <td class="product-price">${new Intl.NumberFormat("en-US",
                        {minimumFractionDigits: 2})
                        .format(cart.product.price)}</td>
                                            <td class="product-quantity">
                                                <div class="quantity-selector">
                                                     <input type="number" class="quantity-input" value="${cart.qty}" disabled="">
                                                </div>
                                            </td>
                                            <td class="product-subtotal">${new Intl.NumberFormat("en-US",
                        {minimumFractionDigits: 2})
                        .format(productSubTotal)}</td>
                                            <td class="product-remove">
                                                <button class="remove-btn" onclick="removeFromCart(${cart.id})"><i class="fas fa-trash-alt"></i></button>
                                            </td>
                                        </tr>
                                    `;
                cart_item_container.innerHTML += tableData;
            });
            document.getElementById("order-total-quantity").innerHTML = totalQty;
            document.getElementById("order-total-quantity-h").innerHTML = totalQty;
            document.getElementById("order-total-amount").innerHTML = new Intl.NumberFormat("en-US",
                    {minimumFractionDigits: 2})
                    .format(total);
        } else {
              Swal.fire({
                icon: 'error',
                title: 'Ops',
                text: json.message

            });
            
        }
    } else {
           Swal.fire({
                icon: 'error',
                title: 'Error',
                text: "Cart Items loading failed..."

            });
     
    }
}

async function  removeFromCart(cartid){
     const response = await fetch("RemoveGromCart?cartid=" + cartid );
    if (response.ok) {
        const json = await response.json(); // await response.text();
        if (json.status) {
            Swal.fire({
                icon: 'success',
                title: 'Remove Success',
                text: json.message,
                showConfirmButton: false

            });

        } else {
            Swal.fire({
                icon: 'warning',
                title: 'Warning',
                text: json.message

            });

        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: "Something went wrong. Try again"

        });
    }
    
}