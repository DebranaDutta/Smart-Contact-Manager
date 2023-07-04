console.log("Manual Script");
const toggleSidebar = () => {
  if ($(".sidebar").is(":visible")) {
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "0%");
  } else {
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "20%");
  }
};
const Search = () => {
  let query = $("#search-input").val();

  if (query == "") {
    $(".search-result").hide();
  } else {
    //console.log(query);
    //Sending req to server
    let url = `http://localhost:9090/search/${query}`;
    fetch(url)
      .then((respose) => {
        return respose.json();
      })
      .then((data) => {
        //console.log(data);
        let text = `<div class='list-group'>`;
        data.forEach((contact) => {
          text += `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
        });
        text += `</div>`;
        $(".search-result").html(text);
        $(".search-result").show();
      });
  }
};
