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
                alert('You must be logged in to delete a survey.');
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
                        alert(errorData.message || 'Failed to delete the survey.');
                    }
                } catch (error) {
                    console.error('Delete Error:', error);
                    alert('An error occurred while deleting the survey.');
                }
            }
        });
    });
});
