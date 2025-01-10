function getSearchState(){
    return JSON.parse(sessionStorage.getItem("searchState"));
}

function getCurrentPage(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    let currentPage = urlParams.get('page');
    if (currentPage == null){
        currentPage = '1'
    }
    return currentPage;
}

function initSearchState(){
    const pathName = window.location.pathname;
    const currentPage = getCurrentPage();
    const value = {"paramPath": pathName, "page":currentPage};
    sessionStorage.setItem("searchState", JSON.stringify(value));
}

document.addEventListener('DOMContentLoaded', function(){
    const searchState = JSON.parse(sessionStorage.getItem("searchState"));
    const queryString = window.location.search;
    if (searchState == null || searchState.paramPath !== window.location.pathname || queryString === ""){
        initSearchState();
        return null;
    }

    searchState.page = getCurrentPage();

    const urlParams = new URLSearchParams(queryString);
    urlParams.forEach(
        (value, key) => {
            searchState[key] = value;
        }
    );
    sessionStorage.setItem("searchState", JSON.stringify(searchState));

})

// document.addEventListener('DOMContentLoaded', function(){
//     const form = document.getElementById('searchForm');
//     const value = getSearchState();
//
//     form.addEventListener('input', () => {
//         form.querySelectorAll('input').forEach((input) => {
//             value[input.name] = input.value;
//         });
//         sessionStorage.setItem("searchState", JSON.stringify(value));
//     });
// });