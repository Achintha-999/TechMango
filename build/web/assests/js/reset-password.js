/* global Swal */
async function resetPassword() {


    const newPassword = document.getElementById("new-password").value;
    const confirmPassword = document.getElementById("confirm-new-password").value;

    const userDataObject = {

        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const userDataJSON = JSON.stringify(userDataObject);

    const response = await fetch("ResetPassword", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: userDataJSON
    });
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
           
            Swal.fire({
                icon: 'success',
                title: 'Password Updated',
                text: json.message

            }).then(() => {

                window.location = "index.html";

            });
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: json.message

            });

        }
    } else {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: "Profile details update failed!"

        });

    }
}

