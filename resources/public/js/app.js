(function () {
   $(".click-once").click(
     function() {
       this.disabled = true;
       this.form.submit();
     });
})();