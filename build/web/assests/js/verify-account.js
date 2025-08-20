/* global Swal */

async function verifyAccount() {

    const verificationCode = document.getElementById("verificationCode").value;



    const verification = {

        verificationCode: verificationCode

    };
    const verificationJson = JSON.stringify(verification);
    const response = await fetch(
            "VerifyAccount",
            {
                method: "POST",
                body: verificationJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.status) {
            Swal.fire({
                icon: 'success',
                title: 'Verification Successful!',
                text: json.message,
                showConfirmButton: false,
                timer: 1500
            }).then(() => {

                window.location = "index.html";


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
            title: 'Oops...',
            text: "Verification failed!",
            confirmButtonText: 'Try Again'
        });
    }
}