/* axios 라이브러리 이용  async/await 이용하면 비동기 처리를 동기화된 코드처럼 작성 가능
    async : 비동기 처리를 위한 함수 명시
    await : async 함수 내에서 비동기 호출하는 부분에 사용
*/

/*
비동기 통신을 하는 부분, reply.js는 비동기통신(호출)만 하고 Promise객체를 렌더링하는쪽에 넘김
결과가 오면 너가 알아서 처리해 이런 느낌
 */

async function get1(bno) {
    const result = await axios.get(`/replies/list/${bno}`); //미리 정의를 해놓고

    // console.log(result);
    return result;
}

// goLast : 마지막 댓글 페이지 호출되도록 하는 변수
async function getList(bno, page, size, goLast) {
    const result = await axios.get(`/replies/list/${bno}`,{param : {page, size}});
    return result.data;
}
