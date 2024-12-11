$(function () {
    const $registerForm = $('#registerForm');
    if($registerForm.length){
        $registerForm.validate({
            rules: {
                name: {
                    required: true,
                    minlength: 2
                },
                email: {
                    required: true,
                    email: true
                },
                password: {
                    required: true,
                    minlength: 4,
                    maxlength: 15
                },
                confirmPassword: {
                    required: true,
                    equalTo: '#password'
                }

            },
            messages: {
                name: {
                    required: '이름을 입력해 주세요',
                    minlength: '적어도 3개의 문자이상 작성해주세요'
                },
                email: {
                    required: '이메일을 입력해 주세요',
                    email: '이메일 형식에 맞게 입력해 주세요'
                },
                password: {
                    required: '패스워드 입력해 주세요',
                    minlength: '패스워드는 최소 4자 이상',
                    maxlength: '패스워드는 최대 15자 까지'
                },
                confirmPassword: {
                    required: '패스워드 확인 입력해 주세요',
                    equalTo: '패스워드가 같지 않습니다!'
                }
            }
        })
    }
})