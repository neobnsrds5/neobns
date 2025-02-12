// 어플리케이션 등록 모달창 열기
document.getElementById('openAppCreateModal').onclick = () => {
    const modalElement = document.getElementById('appCreateModal');
    const applicationModal = new bootstrap.Modal(modalElement);
    applicationModal.show();
}

// 어플리케이션 삭제
const deleteApplication = (applicationId) => {
    const select = confirm("삭제하시겠습니까?")
    if (select) {
        const searchState = JSON.parse(sessionStorage.getItem('searchState'));
        const {paramPath, page, ...params} = searchState;
        location.href = "/admin/flow/delete?applicationId=" + applicationId + "&" + new URLSearchParams(params).toString();
    }
}