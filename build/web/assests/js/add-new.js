/* global Swal */

async  function  addBrand() {

    const brandName = document.getElementById("brandName").value;
    const brand = {
        brandName: brandName
    };

    const brandJson = JSON.stringify(brand);

    const response = await  fetch(
            "AddBrand",
            {
                method: "POST",
                body: brandJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            Swal.fire({
                icon: 'success',
                title: 'Brand has been added!',
                text: json.message

            });
        } else {

            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: json.message,
                confirmButtonText: 'Try Again'
            });
            loadBrandList();
        }
    } else {

        Swal.fire({
            icon: 'error',
            title: 'Server Error',
            text: 'Brand Registration failed. Please try again later.',
            confirmButtonText: 'OK'
        });
    }

}

async function addModel() {
    const brandId = document.getElementById("brand-a").value;
    const modelName = document.getElementById("modelName").value;

    const data = {brandId, modelName};

    const response = await fetch("AddModel", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    });


    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            Swal.fire({
                icon: 'success',
                title: 'Model has been added!',
                text: json.message
            });
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: json.message,
                confirmButtonText: 'Try Again'
            });
        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Server Error',
            text: 'Model registration failed. Please try again later.',
            confirmButtonText: 'OK'
        });
    }
}
