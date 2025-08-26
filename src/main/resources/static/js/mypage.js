document.addEventListener('DOMContentLoaded', function () {
    const deleteButtons = document.querySelectorAll('.delete-survey-btn');

    deleteButtons.forEach(button => {
        button.addEventListener('click', async function (e) {
            e.preventDefault();
            e.stopPropagation();

            const surveyCard = this.closest('.survey-card');
            const surveyId = surveyCard.getAttribute('data-survey-id');
            const jwtToken = localStorage.getItem('jwt');

            if (!jwtToken) {
                alert('설문지를 삭제하려면 로그인이 필요합니다.');
                return;
            }

            if (confirm('이 설문을 정말로 삭제하시겠습니까?')) {
                try {
                    const response = await fetch(`/api/surveys/${surveyId}`, {
                        method: 'DELETE',
                        headers: {
                            'Authorization': 'Bearer ' + jwtToken
                        }
                    });

                    if (response.ok) {
                        surveyCard.remove();
                    } else {
                        const errorData = await response.json();
                        alert(errorData.message || '설문지 삭제에 실패했습니다.');
                    }
                } catch (error) {
                    console.error('Delete Error:', error);
                    alert('설문지를 삭제하는 중 오류가 발생했습니다.');
                }
            }
        });
    });
});
