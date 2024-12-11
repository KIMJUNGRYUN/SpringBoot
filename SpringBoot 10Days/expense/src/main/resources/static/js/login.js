$(function () {
    const $loginForm = $('#loginForm');
    if($loginForm.length){
        $loginForm.validate({
            rules: {
                email: {
                    required: true,
                    email: true
                },
                password: {
                    required: true,
                    minlength: 4,
                    maxlength: 15
                }
            },
            messages: {
                email: {
                    required: '이메일을 입력해 주세요',
                    email: '이메일 형식에 맞게 입력해 주세요'
                },
                password: {
                    required: '패스워드 입력해 주세요',
                    minlength: '패스워드는 최소 4자 이상',
                    maxlength: '패스워드는 최대 15자 까지'
                }
            },
            errorElement: 'span',
            errorPlacement: function (error, element){
                error.addClass('help-block');
                error.insertAfter(element);
            }
        })
    }
})