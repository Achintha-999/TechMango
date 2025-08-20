/* global Swal */

async function verifyAccount() {

    const verificationCode = document.getElementById("verificationCode").value;

    

    const verification = {

        verificationCode: verificationCode

    };
    const verificationJson = JSON.stringify(verification);
    const response = await fetch(
            "VerifyAccountForgotPassword",
            {
                method: "POST",
                body: verificationJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) {

        const json = await  response.json();
        if (json.status) {//if true
            Swal.fire({
                icon: 'success',
                title: 'Verification Successful!',
                text: json.message,
                confirmButtonText: 'Go to Reset Password'
            }).then(() => {
                window.location = "forgot-password.html";
            });
     

        } else {//when status false

            if (json.message === "1") {
                window.location = "sign-in.html";
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: json.message,
                    confirmButtonText: 'Try Again'
                });
                
            }
        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: "Verification failed! ",
            confirmButtonText: 'Try Again'
        });

   

    }
}

