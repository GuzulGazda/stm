$(document).ready(function () {
    registryItemPopup();
    regisryOrderPopup();
    registrySidebarMenu();
    registryAddItemToOrder();

    $('a.show-items').click(function () {
        $.get("cagalog.xhtml", {groupId: 1, page: 7});
    });

    // set search string 
    $("[name='search']").val($('#searchString').val());

//    $('ul.nav-list #').parent().children('ul.tree').toggle(0);
    // Expand selected group
//    var selectedGroup = $('#groupId').val();
//    $('ul.nav-list #' + selectedGroup + '').parent().children('ul.tree').toggle(0);

});

$(window).load(function() {
	$(".loader").fadeOut("slow");
});

function regisryOrderPopup() {
    // ============   Order popup start ==================//
    $('a.order-window').click(function () {
        $.ajax({
            type: "GET",
            url: "templates/orderDialog.xhtml",
            dataType: 'html',
            success: function (html) {
                $('.order-popup').html(html);
                $('body').append('<div id="mask-order"></div>');
                $('#mask-order').fadeIn(600);
                $('.order-popup').slideDown(100);
            },
            error: function () {

            },
            complete: function () {
            }
        });
        return false;
    });

    // When clicking on the button close or the mask layer the popup closed

    $('a.close_order, #mask-order').live('click', function () {
        $('#mask-order , .order-popup').fadeOut(300, function () {
            $('#mask-order').remove();
        });
        return false;
    });
    // ============  Order popup end =====================//
}

function registryItemPopup() {
    // ============   Item popup start ===================//
    $('a.item-window').click(function () {
        // Getting the variable's value from a link 
        var itemId = $(this).attr('itemId');
        $.ajax({
            type: "GET",
            url: "templates/itemDialog.xhtml?itemId=" + itemId,
            dataType: 'html',
            success: function (html) {
                $('.item-popup').html(html);
                $('body').append('<div id="mask-item"></div>');
                $('#mask-item').fadeIn(100);
                $('.item-popup').slideDown(400);
            },
            error: function () {
            },
            complete: function () {
            }
        });
        return false;
    });

    // When clicking on the button close or the mask layer the popup closed

    $('a.close_item, #mask-item').live('click', function () {
        $('#mask-item , .item-popup').fadeOut(100, function () {
            $('#mask-item').remove();
        });
        return false;
    });
    // ============   Item popup end =====================//    
}

function registryAddItemToOrder() {
    // ============  Add item to order start ====================//
    $('a.item-add').click(function () {
        // Getting the variable's value from a link 
        var itemId = $(this).attr('itemId');
        $.get('OrderItemAddServlet', {
            itemId: itemId
        }, function (responseText) {
            var newOrderedItemCount = responseText;
            $('#orderedItemCount').text(newOrderedItemCount);
            $('#itemBuy' + itemId).html('<a href="#order-box" class="order-window"><img src="resources/img/basket_small.png" title="Корзина"/></a>');
            regisryOrderPopup();
        });
        return false;
    });
    // ============  Add item to order end =====================//
}

function registrySidebarMenu() {
    // ============   Sidebar stat =======================//
    $('.tree-toggle').click(function () {
        $(this).parent().children('ul.tree').toggle(0);
    });

    $(function () {
        $('.tree-toggle').parent().children('ul.tree').toggle(0);
    });
    // ============   Sidebar end =======================//
}
