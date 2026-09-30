const stripe = Stripe("pk_test_51TiXru8vIMH9ANyD..."); // Stripeの機能を利用するための公開鍵を設定する
const paymentButton = document.getElementById("paymentButton"); // 画面上の決済ボタンの要素を取得する

paymentButton.addEventListener("click", (event) => { // 決済ボタンがクリックされたときに実行する処理を登録する
    event.preventDefault(); // ボタンをクリックしたときのブラウザの標準的な動作（勝手な画面遷移など）を止める

    stripe.redirectToCheckout({ // Stripeが用意する決済画面（チェックアウトページ）へ移動する
        sessionId: sessionId // サーバーから受け取った決済セッションのIDを指定する
    }).then(function (result) {
        if (result.error) { // 決済画面への移動時にエラーが発生した場合
            alert(result.error.message); // エラーの内容をアラート画面で表示する
        }
    });
});