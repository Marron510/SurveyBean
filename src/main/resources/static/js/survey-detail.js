document.addEventListener('DOMContentLoaded', function () {
    const surveyForm = document.getElementById('surveyForm');
    const messageDiv = document.getElementById('message');

    if (surveyForm) {
        surveyForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            const answers = [];
            const surveyId = window.location.pathname.split('/')[2];
            const jwtToken = localStorage.getItem('jwt');

            if (!jwtToken) {
                messageDiv.innerHTML = '<div style="color: red;">답변을 제출하려면 로그인이 필요합니다.</div>';
                setTimeout(() => { window.location.href = '/login'; }, 2000);
                return;
            }

            document.querySelectorAll('.question-block').forEach(questionBlock => {
                const questionId = questionBlock.getAttribute('data-question-id');
                const questionType = questionBlock.getAttribute('data-question-type');

                if (questionType === 'SINGLE_CHOICE') {
                    const checkedRadio = questionBlock.querySelector('input[type="radio"]:checked');
                    if (checkedRadio) {
                        answers.push({
                            questionId: parseInt(questionId),
                            choiceId: parseInt(checkedRadio.value),
                            content: null
                        });
                    }
                } else if (questionType === 'MULTIPLE_CHOICE') {
                    const checkedBoxes = questionBlock.querySelectorAll('input[type="checkbox"]:checked');
                    checkedBoxes.forEach(checkbox => {
                        answers.push({
                            questionId: parseInt(questionId),
                            choiceId: parseInt(checkbox.value),
                            content: null
                        });
                    });
                } else if (questionType === 'TEXT') {
                    const textArea = questionBlock.querySelector('textarea');
                    if (textArea && textArea.value.trim() !== '') {
                        answers.push({
                            questionId: parseInt(questionId),
                            choiceId: null,
                            content: textArea.value
                        });
                    }
                }
            });

            const submissionData = {
                answers: answers
            };

            try {
                const response = await fetch(`/api/surveys/${surveyId}/responses`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + jwtToken
                    },
                    body: JSON.stringify(submissionData)
                });

                if (response.ok) {
                    messageDiv.innerHTML = '<div style="color: green;">답변이 성공적으로 제출되었습니다! 결과 페이지로 이동합니다...</div>';
                    setTimeout(() => {
                        window.location.href = `/surveys/${surveyId}/results`;
                    }, 2000);
                } else {
                    const errorData = await response.json();
                    throw new Error(errorData.message || '답변 제출에 실패했습니다.');
                }
            } catch (error) {
                messageDiv.innerHTML = '<div style="color: red;">' + error.message + '</div>';
                console.error('Submission Error:', error);
            }
        });
    }
});